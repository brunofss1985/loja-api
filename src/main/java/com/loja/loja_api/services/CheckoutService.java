package com.loja.loja_api.services;

import com.loja.loja_api.dto.CheckoutRequestDTO;
import com.loja.loja_api.dto.PaymentResponseDTO;
import com.loja.loja_api.enums.OrderStatus;
import com.loja.loja_api.enums.PaymentStatus;
import com.loja.loja_api.models.*;
import com.loja.loja_api.repositories.CustomerRepository;
import com.loja.loja_api.repositories.OrderRepository;
import com.loja.loja_api.repositories.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CheckoutService {

    private final OrderRepository orderRepo;
    private final PaymentRepository paymentRepo;
    private final CustomerRepository customerRepo;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${mercadopago.token}")
    private String accessToken;

    private static final DateTimeFormatter MP_DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    @Transactional
    public PaymentResponseDTO checkout(CheckoutRequestDTO req) {
        // 1. Encontrar ou criar cliente
        Customer customer = customerRepo.findByEmail(req.getEmail())
                .orElseGet(() -> {
                    Customer newCustomer = Customer.builder()
                            .fullName(req.getFullName())
                            .email(req.getEmail())
                            .phone(req.getPhone())
                            .cpf(req.getCpf())
                            .build();
                    return customerRepo.save(newCustomer);
                });

        // 2. Criar pedido
        Order order = mapToOrder(req, customer);
        order = orderRepo.save(order);

        // 3. Criar pagamento
        Payment payment = Payment.builder()
                .order(order)
                .method(req.getMethod())
                .installments(req.getInstallments())
                .provider("MERCADO_PAGO")
                .status(PaymentStatus.PENDING)
                .build();

        PaymentResponseDTO response;

        try {
            switch (req.getMethod()) {
                case CREDIT, DEBIT -> response = processCardPayment(req, payment, order);
                case PIX -> response = processPixPayment(req, payment, order);
                case BOLETO -> response = processBoletoPayment(req, payment, order);
                default -> throw new RuntimeException("Método de pagamento não suportado.");
            }
        } catch (Exception e) {
            payment.setStatus(PaymentStatus.DECLINED);
            paymentRepo.save(payment);
            order.setStatus(OrderStatus.CANCELED);
            orderRepo.save(order);
            return PaymentResponseDTO.builder()
                    .status(PaymentStatus.DECLINED)
                    .message("Erro ao processar o pagamento: " + e.getMessage())
                    .build();
        }

        paymentRepo.save(payment);
        return response;
    }

    private Order mapToOrder(CheckoutRequestDTO req, Customer customer) {
        Order order = Order.builder()
                .subtotal(req.getSubtotal())
                .shipping(req.getShipping())
                .discount(req.getDiscount())
                .total(req.getTotal())
                .status(OrderStatus.CREATED)
                .customer(customer)
                .build();

        List<OrderItem> items = req.getItems().stream().map(i ->
                OrderItem.builder()
                        .order(order)
                        .name(i.getName())
                        .quantity(i.getQuantity())
                        .price(i.getPrice())
                        .build()
        ).collect(Collectors.toList());

        order.setItems(items);
        return order;
    }

    // =============================
    // PAGAMENTO COM CARTÃO
    // =============================
    // =============================
// PAGAMENTO COM CARTÃO
// =============================
    private PaymentResponseDTO processCardPayment(CheckoutRequestDTO req, Payment payment, Order order) {
        HttpHeaders headers = baseHeaders();

        JSONObject payer = new JSONObject();
        payer.put("email", req.getEmail());

        JSONObject body = new JSONObject();
        body.put("transaction_amount", order.getTotal());
        body.put("description", "Pagamento de Pedido #" + order.getId());

        // ⚠️ Usa o paymentMethodId do request (visa, master, elo...)
        body.put("payment_method_id", req.getPaymentMethodId());

        // Token gerado pelo SDK do MP no frontend
        body.put("token", req.getCardToken());

        // Parcelas
        body.put("installments", req.getInstallments() != null ? req.getInstallments() : 1);

        body.put("payer", payer);

        HttpEntity<String> entity = new HttpEntity<>(body.toString(), headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    "https://api.mercadopago.com/v1/payments", entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                JSONObject json = new JSONObject(response.getBody());

                PaymentStatus newStatus = PaymentStatus.fromMercadoPagoStatus(json.optString("status"));
                payment.setStatus(newStatus);
                payment.setProviderPaymentId(json.optString("id"));

                if (newStatus == PaymentStatus.APPROVED) {
                    order.setStatus(OrderStatus.PAID);
                } else if (newStatus == PaymentStatus.DECLINED) {
                    order.setStatus(OrderStatus.CANCELED);
                }

                orderRepo.save(order);

                return PaymentResponseDTO.builder()
                        .orderId(order.getId())
                        .status(newStatus)
                        .message(statusMessage(payment))
                        .build();

            } else {
                throw new RuntimeException("Erro ao processar pagamento com cartão: " + response.getBody());
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro de comunicação com a API do Mercado Pago: " + e.getMessage());
        }
    }



    // =============================
    // PIX
    // =============================
    private PaymentResponseDTO processPixPayment(CheckoutRequestDTO req, Payment payment, Order order) {
        try {
            System.out.println("🔄 Iniciando processamento PIX para pedido #" + order.getId());
            HttpHeaders headers = baseHeaders();

            String cpfLimpo = order.getCustomer().getCpf().replaceAll("\\D", "");
            String[] fullNameParts = order.getCustomer().getFullName().split(" ", 2);
            String firstName = fullNameParts[0];
            String lastName = fullNameParts.length > 1 ? fullNameParts[1] : "Cliente";

            JSONObject identification = new JSONObject();
            identification.put("type", "CPF");
            identification.put("number", cpfLimpo);

            JSONObject payer = new JSONObject();
            payer.put("email", order.getCustomer().getEmail());
            payer.put("first_name", firstName);
            payer.put("last_name", lastName);
            payer.put("identification", identification);

            JSONObject body = new JSONObject();
            body.put("transaction_amount", order.getTotal());
            body.put("description", "Pedido #" + order.getId());
            body.put("payment_method_id", "pix");
            body.put("payer", payer);

            // Expiração: 24h no formato correto
            body.put("date_of_expiration", OffsetDateTime.now()
                    .plusDays(1)
                    .format(MP_DATE_FORMAT));

            System.out.println("📤 Enviando requisição PIX para Mercado Pago: " + body.toString());
            HttpEntity<String> entity = new HttpEntity<>(body.toString(), headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    "https://api.mercadopago.com/v1/payments", entity, String.class);

            System.out.println("📥 Resposta do Mercado Pago (PIX): " + response.getStatusCode() + " - " + response.getBody());

            if (response.getStatusCode().is2xxSuccessful()) {
                JSONObject json = new JSONObject(response.getBody());

                // Verificação mais segura do ID
                String paymentId = json.optString("id");
                if (paymentId.isEmpty()) {
                    throw new RuntimeException("ID do pagamento não retornado pelo Mercado Pago");
                }
                payment.setProviderPaymentId(paymentId);

                // Verificação se point_of_interaction existe
                if (!json.has("point_of_interaction")) {
                    throw new RuntimeException("Dados de PIX não retornados pelo Mercado Pago. Response: " + json.toString());
                }

                JSONObject transactionData = json
                        .getJSONObject("point_of_interaction")
                        .getJSONObject("transaction_data");

                String qrCode = transactionData.optString("qr_code", null);
                String qrCodeBase64 = transactionData.optString("qr_code_base64", null);
                
                if (qrCode == null || qrCode.isEmpty()) {
                    throw new RuntimeException("QR Code PIX não foi gerado pelo Mercado Pago");
                }

                payment.setQrCode(qrCode);
                payment.setQrCodeBase64(qrCodeBase64);
                payment.setStatus(PaymentStatus.PENDING);

                System.out.println("✅ PIX gerado com sucesso. QR Code: " + (qrCode != null ? "OK" : "NULL"));

                return PaymentResponseDTO.builder()
                        .orderId(order.getId())
                        .status(PaymentStatus.PENDING)
                        .qrCode(payment.getQrCode())
                        .qrCodeBase64(payment.getQrCodeBase64())
                        .message(statusMessage(payment))
                        .build();
            } else {
                throw new RuntimeException("Erro Mercado Pago (Pix): " + response.getBody());
            }
        } catch (Exception e) {
            System.err.println("❌ Erro ao processar PIX: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erro ao gerar Pix: " + e.getMessage());
        }
    }

    // =============================
    // BOLETO
    // =============================
    private PaymentResponseDTO processBoletoPayment(CheckoutRequestDTO req, Payment payment, Order order) {
        try {
            System.out.println("🔄 Iniciando processamento BOLETO para pedido #" + order.getId());
            HttpHeaders headers = baseHeaders();

            JSONObject body = new JSONObject();
            body.put("transaction_amount", order.getTotal());
            body.put("description", "Pagamento de Pedido #" + order.getId());
            body.put("payment_method_id", "bolbradesco");

            JSONObject payer = new JSONObject();
            payer.put("email", req.getEmail());

            String[] fullNameParts = req.getFullName().split(" ", 2);
            String firstName = fullNameParts[0];
            String lastName = fullNameParts.length > 1 ? fullNameParts[1] : "Cliente";
            payer.put("first_name", firstName);
            payer.put("last_name", lastName);

            JSONObject identification = new JSONObject();
            identification.put("type", "CPF");
            // ✅ Corrigido: limpar CPF como no PIX
            String cpfLimpo = req.getCpf().replaceAll("\\D", "");
            identification.put("number", cpfLimpo);
            payer.put("identification", identification);

            JSONObject address = new JSONObject();
            address.put("zip_code", req.getCep());
            address.put("street_name", req.getStreet());
            address.put("street_number", req.getNumber());
            address.put("neighborhood", req.getNeighborhood());
            address.put("city", req.getCity());
            address.put("federal_unit", req.getState());
            payer.put("address", address);

            body.put("payer", payer);

            // Expira em 3 dias
            body.put("date_of_expiration", OffsetDateTime.now()
                    .plusDays(3)
                    .format(MP_DATE_FORMAT));

            System.out.println("📤 Enviando requisição BOLETO para Mercado Pago: " + body.toString());
            HttpEntity<String> entity = new HttpEntity<>(body.toString(), headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    "https://api.mercadopago.com/v1/payments", entity, String.class);

            System.out.println("📥 Resposta do Mercado Pago (BOLETO): " + response.getStatusCode() + " - " + response.getBody());

            if (response.getStatusCode().is2xxSuccessful()) {
                JSONObject json = new JSONObject(response.getBody());

                PaymentStatus newStatus = PaymentStatus.fromMercadoPagoStatus(json.optString("status"));
                payment.setStatus(newStatus);
                payment.setProviderPaymentId(json.optString("id"));

                // Verificação mais robusta do boleto URL
                String boletoUrl = null;
                if (json.has("transaction_details") && 
                    json.getJSONObject("transaction_details").has("external_resource_url")) {
                    boletoUrl = json.getJSONObject("transaction_details").optString("external_resource_url");
                }
                
                if (boletoUrl == null || boletoUrl.isEmpty()) {
                    System.err.println("⚠️ URL do boleto não foi retornada pelo Mercado Pago. Response: " + json.toString());
                    // Não falha, mas registra o problema
                }
                
                payment.setBoletoUrl(boletoUrl);
                orderRepo.save(order);

                System.out.println("✅ BOLETO gerado com sucesso. URL: " + (boletoUrl != null ? "OK" : "NULL"));

                return PaymentResponseDTO.builder()
                        .orderId(order.getId())
                        .status(newStatus)
                        .boletoUrl(boletoUrl)
                        .message(statusMessage(payment))
                        .build();
            } else {
                throw new RuntimeException("Erro ao gerar boleto: " + response.getBody());
            }
        } catch (Exception e) {
            System.err.println("❌ Erro ao processar BOLETO: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erro de comunicação com a API do Mercado Pago (boleto): " + e.getMessage());
        }
    }

    // =============================
    // HELPERS
    // =============================
    private HttpHeaders baseHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        headers.set("X-Idempotency-Key", UUID.randomUUID().toString());
        return headers;
    }

    private String statusMessage(Payment payment) {
        return switch (payment.getStatus()) {
            case APPROVED -> "Pagamento aprovado";
            case DECLINED -> "Pagamento recusado";
            case PENDING -> "Aguardando pagamento";
            case CANCELED -> "Pagamento cancelado";
        };
    }
}

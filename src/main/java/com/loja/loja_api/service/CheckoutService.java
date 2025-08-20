package com.loja.loja_api.service;

import com.loja.loja_api.dto.CheckoutRequest;
import com.loja.loja_api.enums.OrderStatus;
import com.loja.loja_api.enums.PaymentStatus;
import com.loja.loja_api.model.*;
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

    @Transactional
    public PaymentResponse checkout(CheckoutRequest req) {
        // Passo 1: Encontrar ou criar o cliente
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

        // Passo 2: Mapear o pedido com o cliente
        Order order = mapToOrder(req, customer);
        order = orderRepo.save(order);

        Payment payment = Payment.builder()
                .order(order)
                .method(req.getMethod())
                .installments(req.getInstallments())
                .provider("MERCADO_PAGO")
                .status(PaymentStatus.PENDING)
                .build();

        PaymentResponse response;

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
            return PaymentResponse.builder()
                    .status(PaymentStatus.DECLINED)
                    .message("Erro ao processar o pagamento: " + e.getMessage())
                    .build();
        }

        paymentRepo.save(payment);
        return response;
    }

    private Order mapToOrder(CheckoutRequest req, Customer customer) {
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

    private PaymentResponse processCardPayment(CheckoutRequest req, Payment payment, Order order) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        headers.set("X-Idempotency-Key", UUID.randomUUID().toString());

        JSONObject payer = new JSONObject();
        payer.put("email", req.getEmail());

        JSONObject body = new JSONObject();
        body.put("transaction_amount", order.getTotal());
        body.put("description", "Pagamento de Pedido #" + order.getId());
        body.put("payment_method_id", req.getMethod().name().toLowerCase());
        body.put("token", req.getCardToken());
        body.put("installments", req.getInstallments());
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

                return PaymentResponse.builder()
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

    private PaymentResponse processPixPayment(CheckoutRequest req, Payment payment, Order order) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);
            headers.set("X-Idempotency-Key", UUID.randomUUID().toString());

            String cpfLimpo = order.getCustomer().getCpf().replaceAll("\\D", "");

            JSONObject identification = new JSONObject();
            identification.put("type", "CPF");
            identification.put("number", cpfLimpo);

            JSONObject payer = new JSONObject();
            payer.put("email", order.getCustomer().getEmail());
            payer.put("first_name", order.getCustomer().getFullName());
            payer.put("identification", identification);

            JSONObject body = new JSONObject();
            body.put("transaction_amount", order.getTotal());
            body.put("description", "Pedido #" + order.getId() + " - Suplementos");
            body.put("payment_method_id", "pix");
            body.put("payer", payer);

            HttpEntity<String> entity = new HttpEntity<>(body.toString(), headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    "https://api.mercadopago.com/v1/payments", entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                JSONObject json = new JSONObject(response.getBody());

                payment.setProviderPaymentId(json.get("id").toString());

                JSONObject transactionData = json
                        .getJSONObject("point_of_interaction")
                        .getJSONObject("transaction_data");

                payment.setQrCode(transactionData.optString("qr_code", null));
                payment.setQrCodeBase64(transactionData.optString("qr_code_base64", null));
                payment.setStatus(PaymentStatus.PENDING);

                return PaymentResponse.builder()
                        .orderId(order.getId())
                        .status(PaymentStatus.PENDING)
                        .qrCode(payment.getQrCode())
                        .qrCodeBase64(payment.getQrCodeBase64())
                        .message(statusMessage(payment))
                        .build();
            } else {
                throw new RuntimeException("Erro Mercado Pago: " + response.getBody());
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar Pix: " + e.getMessage());
        }
    }

    private PaymentResponse processBoletoPayment(CheckoutRequest req, Payment payment, Order order) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        headers.set("X-Idempotency-Key", UUID.randomUUID().toString());

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
        identification.put("number", req.getCpf());
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

        HttpEntity<String> entity = new HttpEntity<>(body.toString(), headers);

        System.out.println("JSON enviado para o Mercado Pago: " + body.toString());

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    "https://api.mercadopago.com/v1/payments", entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                JSONObject json = new JSONObject(response.getBody());

                PaymentStatus newStatus = PaymentStatus.fromMercadoPagoStatus(json.optString("status"));
                payment.setStatus(newStatus);
                payment.setProviderPaymentId(json.optString("id"));

                String boletoUrl = json.getJSONObject("transaction_details").optString("external_resource_url");
                payment.setBoletoUrl(boletoUrl);

                orderRepo.save(order);

                return PaymentResponse.builder()
                        .orderId(order.getId())
                        .status(newStatus)
                        .boletoUrl(boletoUrl)
                        .message(statusMessage(payment))
                        .build();
            } else {
                throw new RuntimeException("Erro ao gerar boleto: " + response.getBody());
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro de comunicação com a API do Mercado Pago: " + e.getMessage());
        }
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
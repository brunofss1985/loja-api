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
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CheckoutService {

    private final OrderRepository orderRepo;
    private final PaymentRepository paymentRepo;
    private final CustomerRepository customerRepo; // ✅ novo repositório para evitar duplicar cliente

    @Value("${mercadopago.token}")
    private String accessToken;

    public PaymentResponse checkout(CheckoutRequest req) {
        Order order = mapToOrder(req);
        order = orderRepo.save(order);

        Payment payment = Payment.builder()
                .order(order)
                .method(req.getMethod())
                .installments(req.getInstallments())
                .provider("MERCADO_PAGO")
                .status(PaymentStatus.PENDING)
                .build();

        switch (req.getMethod()) {
            case CREDIT, DEBIT -> simulateCardAuth(req, payment);
            case PIX -> buildPix(payment, order);
            case BOLETO -> simulateBoleto(payment, order);
        }

        payment = paymentRepo.save(payment);

        return PaymentResponse.builder()
                .orderId(order.getId())
                .paymentId(payment.getId())
                .status(payment.getStatus())
                .qrCode(payment.getQrCode())
                .qrCodeBase64(payment.getQrCodeBase64())
                .message(statusMessage(payment))
                .build();
    }

    private void simulateCardAuth(CheckoutRequest req, Payment payment) {
        boolean approved = req.getCardToken() != null || "4242".equals(req.getCardLast4());
        payment.setStatus(approved ? PaymentStatus.APPROVED : PaymentStatus.DECLINED);
        payment.getOrder().setStatus(approved ? OrderStatus.PAID : OrderStatus.CREATED);
    }

    private void buildPix(Payment payment, Order order) {
        try {
            RestTemplate restTemplate = new RestTemplate();

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
            } else {
                throw new RuntimeException("Erro Mercado Pago: " + response.getBody());
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar Pix: " + e.getMessage());
        }
    }

    private void simulateBoleto(Payment payment, Order order) {
        payment.setStatus(PaymentStatus.PENDING);
    }

    private Order mapToOrder(CheckoutRequest req) {
        Customer customer = customerRepo.findByEmail(req.getEmail())
                .map(existing -> {
                    // Se o cliente já existir, atualiza dados faltantes
                    if (req.getCpf() != null && (existing.getCpf() == null || existing.getCpf().isBlank())) {
                        existing.setCpf(req.getCpf());
                    }
                    if (req.getPhone() != null && (existing.getPhone() == null || existing.getPhone().isBlank())) {
                        existing.setPhone(req.getPhone());
                    }
                    return existing;
                })
                .orElse(Customer.builder()
                        .fullName(req.getFullName())
                        .email(req.getEmail())
                        .phone(req.getPhone())
                        .cpf(req.getCpf())
                        .build());

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
        ).toList();

        order.setItems(items);
        return order;
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

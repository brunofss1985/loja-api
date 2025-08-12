package com.loja.loja_api.service;

import com.loja.loja_api.enums.OrderStatus;
import com.loja.loja_api.enums.PaymentStatus;
import com.loja.loja_api.model.Order;
import com.loja.loja_api.model.Payment;
import com.loja.loja_api.repositories.OrderRepository;
import com.loja.loja_api.repositories.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WebhookService {

    private static final Logger logger = LoggerFactory.getLogger(WebhookService.class);

    private final PaymentRepository paymentRepo;
    private final OrderRepository orderRepo;

    @Value("${mercadopago.token}")
    private String accessToken;

    public void confirmarPagamento(Long mercadoPagoPaymentId) {
        logger.info("Confirmando pagamento Mercado Pago ID: {}", mercadoPagoPaymentId);

        // Consulta o pagamento na API do Mercado Pago
        JSONObject json = consultarPagamento(mercadoPagoPaymentId);
        if (json == null) {
            logger.warn("Pagamento não encontrado na API do Mercado Pago");
            return;
        }

        String status = json.optString("status");
        if (!"approved".equalsIgnoreCase(status)) {
            logger.info("Pagamento ainda não aprovado: status = {}", status);
            return;
        }

        String providerPaymentId = String.valueOf(json.get("id"));

        // Busca o pagamento no banco
        Optional<Payment> optPayment = paymentRepo.findByProviderPaymentId(providerPaymentId);
        if (optPayment.isEmpty()) {
            logger.warn("Pagamento com providerPaymentId={} não encontrado no banco", providerPaymentId);
            return;
        }

        Payment payment = optPayment.get();
        if (payment.getStatus() == PaymentStatus.APPROVED) {
            logger.info("Pagamento já está aprovado");
            return;
        }

        // Atualiza status e data de confirmação
        payment.setStatus(PaymentStatus.APPROVED);
        payment.setConfirmedAt(Instant.now());
        paymentRepo.save(payment);

        // Atualiza status do pedido
        Order order = payment.getOrder();
        order.setStatus(OrderStatus.PAID);
        orderRepo.save(order);

        logger.info("Pagamento confirmado e pedido atualizado: Order ID {}", order.getId());
    }

    private JSONObject consultarPagamento(Long paymentId) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    "https://api.mercadopago.com/v1/payments/" + paymentId,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                logger.error("Erro ao consultar pagamento: {}", response.getBody());
                return null;
            }

            return new JSONObject(response.getBody());
        } catch (Exception e) {
            logger.error("Erro ao consultar pagamento Mercado Pago", e);
            return null;
        }
    }
}

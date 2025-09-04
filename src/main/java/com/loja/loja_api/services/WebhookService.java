package com.loja.loja_api.services;

import com.loja.loja_api.enums.OrderStatus;
import com.loja.loja_api.enums.PaymentStatus;
import com.loja.loja_api.models.Order;
import com.loja.loja_api.models.Payment;
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
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${mercadopago.token}")
    private String accessToken;

    /**
     * Processa o ID de pagamento do Mercado Pago para confirmar o status.
     * Este método é o ponto de entrada chamado pelo WebhookController.
     * @param mercadoPagoPaymentId O ID do pagamento extraído do payload do webhook.
     */
    public void processPaymentWebhook(Long mercadoPagoPaymentId) {
        logger.info("Iniciando processamento do webhook para o ID do pagamento: {}", mercadoPagoPaymentId);
        confirmarPagamento(mercadoPagoPaymentId);
    }

    private void confirmarPagamento(Long mercadoPagoPaymentId) {
        logger.info("Consultando e confirmando pagamento Mercado Pago ID: {}", mercadoPagoPaymentId);

        JSONObject json = consultarPagamento(mercadoPagoPaymentId);
        if (json == null) {
            logger.warn("Pagamento com ID {} não encontrado na API do Mercado Pago", mercadoPagoPaymentId);
            return;
        }

        String status = json.optString("status");
        String providerPaymentId = String.valueOf(json.optString("id"));

        Optional<Payment> optPayment = paymentRepo.findByProviderPaymentId(providerPaymentId);
        if (optPayment.isEmpty()) {
            logger.warn("Pagamento com providerPaymentId={} não encontrado no banco de dados local.", providerPaymentId);
            return;
        }

        Payment payment = optPayment.get();

        if (payment.getStatus().name().equalsIgnoreCase(status)) {
            logger.info("Status do pagamento já está atualizado. ID do pedido: {}", payment.getOrder().getId());
            return;
        }

        PaymentStatus newStatus = PaymentStatus.fromMercadoPagoStatus(status);
        payment.setStatus(newStatus);

        Order order = payment.getOrder();
        if (newStatus == PaymentStatus.APPROVED) {
            payment.setConfirmedAt(Instant.now());
            order.setStatus(OrderStatus.PAID);
        } else if (newStatus == PaymentStatus.DECLINED) {
            order.setStatus(OrderStatus.CANCELED);
        }

        paymentRepo.save(payment);
        orderRepo.save(order);

        logger.info("Status do pagamento e pedido atualizados para '{}'. Order ID {}", newStatus, order.getId());
    }

    private JSONObject consultarPagamento(Long paymentId) {
        try {
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
                logger.error("Erro ao consultar pagamento {}: {}", paymentId, response.getBody());
                return null;
            }

            return new JSONObject(response.getBody());
        } catch (Exception e) {
            logger.error("Erro ao consultar pagamento Mercado Pago com ID {}", paymentId, e);
            return null;
        }
    }
}
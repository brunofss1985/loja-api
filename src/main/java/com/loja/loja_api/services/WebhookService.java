package com.loja.loja_api.services;

import com.loja.loja_api.enums.OrderStatus;
import com.loja.loja_api.enums.PaymentStatus;
import com.loja.loja_api.models.Order;
import com.loja.loja_api.models.Payment;
import com.loja.loja_api.repositories.OrderRepository;
import com.loja.loja_api.repositories.PaymentRepository;
import com.loja.loja_api.repositories.OrderStatusHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WebhookService {

    private static final Logger logger = LoggerFactory.getLogger(WebhookService.class);

    private final PaymentRepository paymentRepo;
    private final OrderRepository orderRepo;
    private final OrderStatusHistoryRepository statusHistoryRepo;

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${mercadopago.token}")
    private String accessToken;

    @Transactional
    public void processPaymentWebhook(Long mercadoPagoPaymentId) {
        try {
            logger.info("🔔 Webhook recebido para pagamento ID: {}", mercadoPagoPaymentId);

            // Buscar dados do pagamento na API do Mercado Pago
            JSONObject mpResponse = fetchPaymentStatusFromMP(mercadoPagoPaymentId);
            String status = mpResponse.optString("status");

            logger.info("🔍 Status retornado do MP: {}", status);

            // Verificar se o pagamento existe na base
            Optional<Payment> paymentOpt = paymentRepo.findByProviderPaymentId(String.valueOf(mercadoPagoPaymentId));

            if (paymentOpt.isEmpty()) {
                logger.warn("⚠️ Pagamento com providerPaymentId={} não encontrado.", mercadoPagoPaymentId);
                return;
            }

            Payment payment = paymentOpt.get();
            Order order = payment.getOrder();

            // Mapear status do MP para enum interno
            PaymentStatus newPaymentStatus = PaymentStatus.fromMercadoPagoStatus(status);
            payment.setStatus(newPaymentStatus);

            // Atualiza status do pedido com base no status do pagamento
            switch (newPaymentStatus) {
                case APPROVED -> {
                    order.setStatus(OrderStatus.PAID);
                    saveStatus(order, OrderStatus.PAID);
                }
                case CANCELED, DECLINED -> {
                    order.setStatus(OrderStatus.CANCELED);
                    saveStatus(order, OrderStatus.CANCELED);
                }
                case PENDING -> {
                    order.setStatus(OrderStatus.CREATED); // ou manter CREATED
                }
            }

            // Persistir atualizações
            paymentRepo.save(payment);
            orderRepo.save(order);

            logger.info("✅ Status do pedido e pagamento atualizados com sucesso.");
        } catch (Exception e) {
            logger.error("❌ Erro ao processar webhook: {}", e.getMessage(), e);
        }
    }

    private JSONObject fetchPaymentStatusFromMP(Long paymentId) {
        String url = "https://api.mercadopago.com/v1/payments/" + paymentId;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Erro ao consultar status no Mercado Pago: " + response.getBody());
        }

        return new JSONObject(response.getBody());
    }

    private void saveStatus(Order order, OrderStatus status) {
        order.getStatusHistory().add(
                com.loja.loja_api.models.OrderStatusHistory.builder()
                        .order(order)
                        .status(status.name())
                        .changedAt(java.time.Instant.now())
                        .build()
        );
        statusHistoryRepo.saveAll(order.getStatusHistory());
    }
}

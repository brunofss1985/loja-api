package com.loja.loja_api.controllers;

import com.loja.loja_api.services.WebhookService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/webhook")
@RequiredArgsConstructor
public class WebhookController {

    private static final Logger logger = LoggerFactory.getLogger(WebhookController.class);

    private final WebhookService webhookService;

    @PostMapping
    public ResponseEntity<Void> receberNotificacao(@RequestBody Map<String, Object> payload) {
        try {
            String topic = (String) payload.get("topic");
            String resource = (String) payload.get("resource");

            if (!"payment".equalsIgnoreCase(topic) || resource == null || resource.isBlank()) {
                logger.warn("Webhook ignorado: tópico '{}' inválido ou resource ausente.", topic);
                return ResponseEntity.ok().build();
            }

            // O ID do recurso vem como uma URL, ex: https://api.mercadopago.com/v1/payments/12345
            // Extraímos o ID no final da string
            Long mercadoPagoPaymentId = Long.valueOf(resource.substring(resource.lastIndexOf("/") + 1));

            webhookService.processPaymentWebhook(mercadoPagoPaymentId);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Erro ao processar webhook: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
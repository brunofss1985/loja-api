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
            logger.info("🔔 Webhook recebido: {}", payload);

            String topic = (String) payload.get("type"); // ← "type" no simulador
            Object dataObj = payload.get("data");

            if (!"payment".equalsIgnoreCase(topic) || !(dataObj instanceof Map)) {
                logger.warn("Webhook ignorado: tipo '{}' inválido ou data ausente.", topic);
                return ResponseEntity.ok().build();
            }

            Map<String, Object> dataMap = (Map<String, Object>) dataObj;
            Object idObj = dataMap.get("id");

            if (idObj == null) {
                logger.warn("Webhook ignorado: ID não encontrado em 'data'.");
                return ResponseEntity.ok().build();
            }

            Long mercadoPagoPaymentId = Long.valueOf(idObj.toString());
            webhookService.processPaymentWebhook(mercadoPagoPaymentId);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("❌ Erro ao processar webhook: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }
}

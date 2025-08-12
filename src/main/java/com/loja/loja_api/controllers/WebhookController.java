package com.loja.loja_api.controllers;

import com.loja.loja_api.service.WebhookService;
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
            String type = (String) payload.get("type");
            if (!"payment".equalsIgnoreCase(type)) {
                logger.warn("Webhook ignorado: tipo não é 'payment' → {}", type);
                return ResponseEntity.ok().build();
            }

            Map<String, Object> data = (Map<String, Object>) payload.get("data");
            if (data == null || !data.containsKey("id")) {
                logger.warn("Webhook inválido: campo 'data.id' ausente");
                return ResponseEntity.badRequest().build();
            }

            Long mercadoPagoPaymentId = Long.valueOf(data.get("id").toString());

            webhookService.confirmarPagamento(mercadoPagoPaymentId);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Erro ao processar webhook", e);
            return ResponseEntity.badRequest().build();
        }
    }
}

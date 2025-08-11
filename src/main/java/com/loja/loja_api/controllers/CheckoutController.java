package com.loja.loja_api.controllers;

import com.loja.loja_api.dto.CheckoutRequest;
import com.loja.loja_api.model.PaymentResponse;
import com.loja.loja_api.service.CheckoutService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final CheckoutService checkoutService;

    @PostMapping
    public ResponseEntity<PaymentResponse> checkout(@Valid @RequestBody CheckoutRequest req) {
        PaymentResponse resp = checkoutService.checkout(req);
        return ResponseEntity.ok(resp);
    }

    // Webhook do provedor (substitua por rota real do seu gateway)
    @PostMapping("/webhooks/payment")
    public ResponseEntity<Void> webhook(@RequestBody Map<String, Object> payload) {
        // TODO: validar assinatura, localizar payment por providerPaymentId,
        // atualizar status para APPROVED/DECLINED e order.status para PAID se aprovado.
        return ResponseEntity.ok().build();
    }
}

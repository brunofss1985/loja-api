package com.loja.loja_api.controllers;

import com.loja.loja_api.dto.CheckoutRequest;
import com.loja.loja_api.model.PaymentResponse;
import com.loja.loja_api.service.CheckoutService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
package com.loja.loja_api.controllers;

import com.loja.loja_api.dto.CheckoutRequestDTO;
import com.loja.loja_api.dto.PaymentResponseDTO;
import com.loja.loja_api.services.CheckoutService;
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
    public ResponseEntity<PaymentResponseDTO> checkout(@Valid @RequestBody CheckoutRequestDTO req) {
        PaymentResponseDTO resp = checkoutService.checkout(req);
        return ResponseEntity.ok(resp);
    }
}
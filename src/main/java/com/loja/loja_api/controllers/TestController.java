package com.loja.loja_api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test")
    public ResponseEntity<String> testChat() {
        System.out.println("=== CHAT TEST FUNCIONOU ===");
        return ResponseEntity.ok("Chat endpoint funcionando!");
    }}
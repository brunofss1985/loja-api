package com.loja.loja_api.controllers;

import com.loja.loja_api.model.PagamentoResponse;
import com.loja.loja_api.service.GatewayService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PagamentoController {

    private final GatewayService gatewayService;

    public PagamentoController(GatewayService gatewayService) {
        this.gatewayService = gatewayService;
    }

    @GetMapping("/pagamento")
    public PagamentoResponse gerarPagamento() {
        return gatewayService.criarPagamento();
    }
}

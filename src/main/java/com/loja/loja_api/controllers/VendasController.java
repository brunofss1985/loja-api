package com.loja.loja_api.controllers;

import com.loja.loja_api.dto.ProdutoVendidoDTO;
import com.loja.loja_api.models.ProdutoVendido;
import com.loja.loja_api.repositories.ProdutoVendidoRepository;
import com.loja.loja_api.services.VendaService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/vendas")
@RequiredArgsConstructor
public class VendasController {

    private final VendaService vendaService;
    private final ProdutoVendidoRepository vendidoRepo;

    @PostMapping("/finalizar")
    public ResponseEntity<ProdutoVendidoDTO> finalizar(@RequestBody FinalizaVendaRequest req) {
        ProdutoVendidoDTO dto = vendaService.finalizarVenda(req.getOrderId(), req.getCodigoBarras(), req.getLoteCodigo());
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public List<ProdutoVendidoDTO> listar() {
        return vendidoRepo.findAll().stream()
                .map(ProdutoVendidoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Data
    public static class FinalizaVendaRequest {
        private Long orderId;
        private String codigoBarras;
        private String loteCodigo;
    }
}

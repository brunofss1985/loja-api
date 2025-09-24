package com.loja.loja_api.controllers;

import com.loja.loja_api.dto.ProdutoRealDTO;
import com.loja.loja_api.services.ProdutoRealService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/produto-real")
@RequiredArgsConstructor
public class ProdutoRealController {

    private final ProdutoRealService service;

    @PostMapping
    public ProdutoRealDTO criar(@RequestBody ProdutoRealDTO dto) {
        return service.salvar(dto);
    }

    @GetMapping("/lote/{loteId}")
    public List<ProdutoRealDTO> listarPorLote(@PathVariable Long loteId) {
        return service.listarPorLote(loteId);
    }

    @GetMapping("/estoque-total/{produtoId}")
    public Map<String, Integer> obterEstoqueTotal(@PathVariable Long produtoId) {
        int estoqueTotal = service.obterEstoqueTotalPorProdutoId(produtoId);
        return Map.of("estoqueTotal", estoqueTotal);
    }


    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}

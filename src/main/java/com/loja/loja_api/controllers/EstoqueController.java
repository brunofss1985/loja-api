
package com.loja.loja_api.controllers;

import com.loja.loja_api.models.MovimentacaoEstoque;
import com.loja.loja_api.models.Produto;
import com.loja.loja_api.repositories.MovimentacaoEstoqueRepository;
import com.loja.loja_api.services.ValidadeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estoque")
public class EstoqueController {

    @Autowired
    private MovimentacaoEstoqueRepository movimentacaoRepository;

    @Autowired
    private ValidadeService validadeService;

    @GetMapping("/movimentacoes-por-lote")
    public ResponseEntity<List<MovimentacaoEstoque>> buscarPorLote(@RequestParam String lote) {
        return ResponseEntity.ok(movimentacaoRepository.findByLote(lote));
    }

    @GetMapping("/validade-proxima")
    public ResponseEntity<List<Produto>> listarProdutosComValidadeProxima(@RequestParam(defaultValue = "30") int dias) {
        return ResponseEntity.ok(validadeService.produtosComValidadeProxima(dias));
    }
}

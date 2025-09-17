package com.loja.loja_api.controllers;

import com.loja.loja_api.dto.LoteDTO;
import com.loja.loja_api.services.LoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lotes")
@RequiredArgsConstructor
public class LoteController {

    private final LoteService loteService;

    @GetMapping
    public ResponseEntity<List<LoteDTO>> listarTodos() {
        return ResponseEntity.ok(loteService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoteDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(loteService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<LoteDTO> criar(@RequestBody LoteDTO dto) {
        return ResponseEntity.ok(loteService.criar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LoteDTO> atualizar(@PathVariable Long id, @RequestBody LoteDTO dto) {
        return ResponseEntity.ok(loteService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        loteService.remover(id);
        return ResponseEntity.noContent().build();
    }
}

package com.loja.loja_api.controllers;

import com.loja.loja_api.dto.ProdutoDTO;
import com.loja.loja_api.model.Produto;
import com.loja.loja_api.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService service;

    @GetMapping
    public ResponseEntity<List<Produto>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<Produto>> buscarPorCategoria(@PathVariable String categoria) {
        List<Produto> produtos = service.buscarPorCategoria(categoria);
        return ResponseEntity.ok(produtos);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<Produto> criar(
            @RequestPart("produto") ProdutoDTO dto,
            @RequestPart(value = "imagem", required = false) MultipartFile imagem,
            @RequestPart(value = "galeria", required = false) List<MultipartFile> galeria) {
        return ResponseEntity.ok(service.salvar(dto, imagem, galeria));
    }

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<Produto> atualizar(
            @PathVariable Long id,
            @RequestPart("produto") ProdutoDTO dto,
            @RequestPart(value = "imagem", required = false) MultipartFile imagem,
            @RequestPart(value = "galeria", required = false) List<MultipartFile> galeria) {
        return ResponseEntity.ok(service.atualizar(id, dto, imagem, galeria));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }





}

package com.loja.loja_api.controllers;

import com.loja.loja_api.dto.ProdutoDTO;
import com.loja.loja_api.model.Produto;
import com.loja.loja_api.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService service;

    @GetMapping
    public ResponseEntity<Page<Produto>> listarComFiltros(
            @RequestParam(required = false) List<String> categorias,
            @RequestParam(required = false) List<String> marcas,
            @RequestParam(defaultValue = "0.0") double minPreco,
            @RequestParam(defaultValue = "999999.99") double maxPreco,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Pageable pageable) { // Certifique-se de que Pageable está sendo injetado

        Page<Produto> produtos = service.buscarProdutosComFiltros(categorias, marcas, minPreco, maxPreco, page, size);
        return ResponseEntity.ok(produtos);
    }

    @GetMapping("/marcas")
    public ResponseEntity<List<String>> listarMarcas() {
        return ResponseEntity.ok(service.listarMarcas());
    }

    @GetMapping("/categorias")
    public ResponseEntity<List<String>> listarCategorias() {
        return ResponseEntity.ok(service.listarCategorias());
    }

    // ✨ NOVO: Endpoint para listar marcas por categorias selecionadas
    @GetMapping("/marcas-por-categoria")
    public ResponseEntity<List<String>> listarMarcasPorCategorias(
            @RequestParam(required = false) List<String> categorias) {
        return ResponseEntity.ok(service.listarMarcasPorCategorias(categorias));
    }

    // ✨ NOVO: Endpoint para listar categorias por marcas selecionadas
    @GetMapping("/categorias-por-marca")
    public ResponseEntity<List<String>> listarCategoriasPorMarcas(
            @RequestParam(required = false) List<String> marcas) {
        return ResponseEntity.ok(service.listarCategoriasPorMarcas(marcas));
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
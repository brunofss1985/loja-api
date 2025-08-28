// src/main/java/com/loja/loja_api/controllers/ProdutoController.java

package com.loja.loja_api.controllers;

import com.loja.loja_api.dto.ProdutoDTO;
import com.loja.loja_api.model.Produto;
import com.loja.loja_api.service.ProdutoService;
import com.loja.loja_api.dto.CountedItemDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
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
            @RequestParam(defaultValue = "10") int size
    ) {
        List<String> categoriasNorm = normalizeCommaAndRepeatParams(categorias);
        List<String> marcasNorm = normalizeCommaAndRepeatParams(marcas);

        Page<Produto> produtos = service.buscarProdutosComFiltros(
                categoriasNorm,
                marcasNorm,
                minPreco,
                maxPreco,
                page,
                size
        );

        return ResponseEntity.ok(produtos);
    }

    // Endpoints para listar marcas/categorias com contagem de produtos
    @GetMapping("/marcas")
    public ResponseEntity<List<CountedItemDto>> listarMarcas() {
        return ResponseEntity.ok(service.listarMarcas());
    }

    @GetMapping("/categorias")
    public ResponseEntity<List<CountedItemDto>> listarCategorias() {
        return ResponseEntity.ok(service.listarCategorias());
    }

    @GetMapping("/marcas-por-categoria")
    public ResponseEntity<List<CountedItemDto>> listarMarcasPorCategorias(
            @RequestParam(required = false) List<String> categorias
    ) {
        List<String> categoriasNorm = normalizeCommaAndRepeatParams(categorias);
        return ResponseEntity.ok(service.listarMarcasPorCategorias(categoriasNorm));
    }

    @GetMapping("/categorias-por-marca")
    public ResponseEntity<List<CountedItemDto>> listarCategoriasPorMarcas(
            @RequestParam(required = false) List<String> marcas
    ) {
        List<String> marcasNorm = normalizeCommaAndRepeatParams(marcas);
        return ResponseEntity.ok(service.listarCategoriasPorMarcas(marcasNorm));
    }

    // Novos endpoints para a contagem total
    @GetMapping("/marcas/count")
    public ResponseEntity<Long> contarMarcas() {
        return ResponseEntity.ok(service.contarMarcas());
    }

    @GetMapping("/categorias/count")
    public ResponseEntity<Long> contarCategorias() {
        return ResponseEntity.ok(service.contarCategorias());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<Produto> criar(
            @RequestPart("produto") ProdutoDTO dto,
            @RequestPart(value = "imagem", required = false) MultipartFile imagem,
            @RequestPart(value = "galeria", required = false) List<MultipartFile> galeria
    ) {
        return ResponseEntity.ok(service.salvar(dto, imagem, galeria));
    }

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<Produto> atualizar(
            @PathVariable Long id,
            @RequestPart("produto") ProdutoDTO dto,
            @RequestPart(value = "imagem", required = false) MultipartFile imagem,
            @RequestPart(value = "galeria", required = false) List<MultipartFile> galeria
    ) {
        return ResponseEntity.ok(service.atualizar(id, dto, imagem, galeria));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    private List<String> normalizeCommaAndRepeatParams(List<String> raw) {
        if (raw == null || raw.isEmpty()) return null;
        List<String> out = new ArrayList<>();
        for (String item : raw) {
            if (item == null) continue;
            String[] parts = item.split(",");
            for (String p : parts) {
                String t = p.trim();
                if (!t.isEmpty()) out.add(t);
            }
        }
        return out.isEmpty() ? null : out;
    }
}
package com.loja.loja_api.controllers;

import com.loja.loja_api.dto.ProdutoDTO;
import com.loja.loja_api.dto.CountedItemDTO;
import com.loja.loja_api.models.Produto;
import com.loja.loja_api.services.FiltroService;
import com.loja.loja_api.services.ProdutoService;
import com.loja.loja_api.util.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService service;

    @Autowired
    private FiltroService filtroService;

    // 🧠 ✅ LISTAGEM DE PRODUTOS COM DTO PARA EVITAR 403/LAZY PROBLEMAS
    @GetMapping
    public ResponseEntity<Page<ProdutoDTO>> listarComFiltros(
            @RequestParam(required = false) List<String> categorias,
            @RequestParam(required = false) List<String> marcas,
            @RequestParam(required = false) List<String> objetivos,
            @RequestParam(defaultValue = "0.0") double minPreco,
            @RequestParam(defaultValue = "999999.99") double maxPreco,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) Boolean destaque
    ) {
        List<String> categoriasNorm = ListUtils.normalizeList(categorias);
        List<String> marcasNorm = ListUtils.normalizeList(marcas);
        List<String> objetivosNorm = ListUtils.normalizeList(objetivos);

        Page<Produto> produtos = service.buscarProdutosComFiltros(
                categoriasNorm, marcasNorm, objetivosNorm, minPreco, maxPreco, page, size, sort, destaque
        );

        Page<ProdutoDTO> dtoPage = produtos.map(ProdutoDTO::fromEntity);
        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProdutoDTO>> buscarPorTermo(
            @RequestParam String termo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort
    ) {
        Page<Produto> produtos = service.buscarPorTermo(termo, page, size, sort);
        return ResponseEntity.ok(produtos.map(ProdutoDTO::fromEntity));
    }

    @GetMapping("/destaques")
    public ResponseEntity<Page<ProdutoDTO>> buscarProdutosEmDestaque(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Produto> produtos = service.buscarProdutosEmDestaque(page, size);
        return ResponseEntity.ok(produtos.map(ProdutoDTO::fromEntity));
    }

    @GetMapping("/marcas")
    public ResponseEntity<List<CountedItemDTO>> listarMarcas() {
        return ResponseEntity.ok(filtroService.listarMarcas());
    }

    @GetMapping("/categorias")
    public ResponseEntity<List<CountedItemDTO>> listarCategorias() {
        return ResponseEntity.ok(filtroService.listarCategorias());
    }

    @GetMapping("/marcas-por-categoria")
    public ResponseEntity<List<CountedItemDTO>> listarMarcasPorCategorias(
            @RequestParam(required = false) List<String> categorias
    ) {
        List<String> categoriasNorm = ListUtils.normalizeList(categorias);
        return ResponseEntity.ok(filtroService.listarMarcasPorCategorias(categoriasNorm));
    }

    @GetMapping("/categorias-por-marca")
    public ResponseEntity<List<CountedItemDTO>> listarCategoriasPorMarcas(
            @RequestParam(required = false) List<String> marcas
    ) {
        List<String> marcasNorm = ListUtils.normalizeList(marcas);
        return ResponseEntity.ok(filtroService.listarCategoriasPorMarcas(marcasNorm));
    }

    @GetMapping("/objetivos")
    public ResponseEntity<List<CountedItemDTO>> listarObjetivos() {
        return ResponseEntity.ok(filtroService.listarObjetivos());
    }

    @GetMapping("/objetivos-por-categoria")
    public ResponseEntity<List<CountedItemDTO>> listarObjetivosPorCategorias(
            @RequestParam(required = false) List<String> categorias
    ) {
        List<String> categoriasNorm = ListUtils.normalizeList(categorias);
        return ResponseEntity.ok(filtroService.listarObjetivosPorCategorias(categoriasNorm));
    }

    @GetMapping("/marcas/count")
    public ResponseEntity<Long> contarMarcas() {
        return ResponseEntity.ok(filtroService.contarMarcas());
    }

    @GetMapping("/categorias/count")
    public ResponseEntity<Long> contarCategorias() {
        return ResponseEntity.ok(filtroService.contarCategorias());
    }

    @GetMapping("/objetivos/count")
    public ResponseEntity<Long> contarObjetivos() {
        return ResponseEntity.ok(filtroService.contarObjetivos());
    }

    // ✅ Alterado para retornar ProdutoDTO
    @GetMapping("/{id}")
    public ResponseEntity<ProdutoDTO> buscarPorId(@PathVariable Long id) {
        Produto produto = service.buscarPorId(id);
        return ResponseEntity.ok(ProdutoDTO.fromEntity(produto));
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
}

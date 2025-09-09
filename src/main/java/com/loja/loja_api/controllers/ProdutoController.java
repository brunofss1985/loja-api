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
        return ResponseEntity.ok(service.buscarProdutosComFiltros(
                ListUtils.normalizeList(categorias),
                ListUtils.normalizeList(marcas),
                ListUtils.normalizeList(objetivos),
                minPreco,
                maxPreco,
                page,
                size,
                sort,
                destaque
        ));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProdutoDTO>> buscarPorTermo(
            @RequestParam String termo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort
    ) {
        return ResponseEntity.ok(service.buscarPorTermo(termo, page, size, sort));
    }

    @GetMapping("/destaques")
    public ResponseEntity<Page<ProdutoDTO>> buscarProdutosEmDestaque(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(service.buscarProdutosEmDestaque(page, size));
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
        return ResponseEntity.ok(filtroService.listarMarcasPorCategorias(ListUtils.normalizeList(categorias)));
    }

    @GetMapping("/categorias-por-marca")
    public ResponseEntity<List<CountedItemDTO>> listarCategoriasPorMarcas(
            @RequestParam(required = false) List<String> marcas
    ) {
        return ResponseEntity.ok(filtroService.listarCategoriasPorMarcas(ListUtils.normalizeList(marcas)));
    }

    @GetMapping("/objetivos")
    public ResponseEntity<List<CountedItemDTO>> listarObjetivos() {
        return ResponseEntity.ok(filtroService.listarObjetivos());
    }

    @GetMapping("/objetivos-por-categoria")
    public ResponseEntity<List<CountedItemDTO>> listarObjetivosPorCategorias(
            @RequestParam(required = false) List<String> categorias
    ) {
        return ResponseEntity.ok(filtroService.listarObjetivosPorCategorias(ListUtils.normalizeList(categorias)));
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

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<ProdutoDTO> criar(
            @RequestPart("produto") ProdutoDTO dto,
            @RequestPart(value = "imagem", required = false) MultipartFile imagem,
            @RequestPart(value = "galeria", required = false) List<MultipartFile> galeria
    ) {
        return ResponseEntity.ok(service.salvar(dto, imagem, galeria));
    }

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<ProdutoDTO> atualizar(
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

package com.loja.loja_api.service;

import com.loja.loja_api.dto.ProdutoDTO;
import com.loja.loja_api.model.Produto;
import com.loja.loja_api.repositories.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository repository;

    public List<Produto> listarTodos() {
        return repository.findAll();
    }

    public Produto buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
    }

    public Produto salvar(ProdutoDTO dto) {
        Produto produto = Produto.builder()
                .nome(dto.getNome())
                .slug(dto.getSlug())
                .descricao(dto.getDescricao())
                .descricaoCurta(dto.getDescricaoCurta())
                .categoria(dto.getCategoria())
                .tags(dto.getTags())
                .peso(dto.getPeso())
                .sabor(dto.getSabor())
                .tamanhoPorcao(dto.getTamanhoPorcao())
                .ingredientes(dto.getIngredientes())
                .tabelaNutricional(dto.getTabelaNutricional())
                .preco(dto.getPreco())
                .precoDesconto(dto.getPrecoDesconto())
                .estoque(dto.getEstoque())
                .qtdMinimaEstoque(dto.getQtdMinimaEstoque())
                .custo(dto.getCusto())
                .fornecedor(dto.getFornecedor())
                .lucroEstimado(dto.getLucroEstimado())
                .sku(dto.getSku())
                .codigoBarras(dto.getCodigoBarras())
                .imagemUrl(dto.getImagemUrl())
                .galeria(dto.getGaleria())
                .destaque(dto.getDestaque())
                .novoLancamento(dto.getNovoLancamento())
                .maisVendido(dto.getMaisVendido())
                .promocaoAtiva(dto.getPromocaoAtiva())
                .statusAprovacao(dto.getStatusAprovacao())
                .publicado(dto.getPublicado())
                .avaliacaoMedia(dto.getAvaliacaoMedia())
                .quantidadeAvaliacoes(dto.getQuantidadeAvaliacoes())
                .ativo(dto.getAtivo())
                .quantidadeVendida(dto.getQuantidadeVendida())
                .comentariosAdmin(dto.getComentariosAdmin())
                .criadoEm(new java.util.Date())
                .atualizadoEm(new java.util.Date())
                .build();
        return repository.save(produto);
    }

    public Produto atualizar(Long id, ProdutoDTO dto) {
        Produto produto = buscarPorId(id);
        produto.setNome(dto.getNome());
        produto.setSlug(dto.getSlug());
        produto.setDescricao(dto.getDescricao());
        produto.setDescricaoCurta(dto.getDescricaoCurta());
        produto.setCategoria(dto.getCategoria());
        produto.setTags(dto.getTags());
        produto.setPeso(dto.getPeso());
        produto.setSabor(dto.getSabor());
        produto.setTamanhoPorcao(dto.getTamanhoPorcao());
        produto.setIngredientes(dto.getIngredientes());
        produto.setTabelaNutricional(dto.getTabelaNutricional());
        produto.setPreco(dto.getPreco());
        produto.setPrecoDesconto(dto.getPrecoDesconto());
        produto.setEstoque(dto.getEstoque());
        produto.setQtdMinimaEstoque(dto.getQtdMinimaEstoque());
        produto.setCusto(dto.getCusto());
        produto.setFornecedor(dto.getFornecedor());
        produto.setLucroEstimado(dto.getLucroEstimado());
        produto.setSku(dto.getSku());
        produto.setCodigoBarras(dto.getCodigoBarras());
        produto.setImagemUrl(dto.getImagemUrl());
        produto.setGaleria(dto.getGaleria());
        produto.setDestaque(dto.getDestaque());
        produto.setNovoLancamento(dto.getNovoLancamento());
        produto.setMaisVendido(dto.getMaisVendido());
        produto.setPromocaoAtiva(dto.getPromocaoAtiva());
        produto.setStatusAprovacao(dto.getStatusAprovacao());
        produto.setPublicado(dto.getPublicado());
        produto.setAvaliacaoMedia(dto.getAvaliacaoMedia());
        produto.setQuantidadeAvaliacoes(dto.getQuantidadeAvaliacoes());
        produto.setAtivo(dto.getAtivo());
        produto.setQuantidadeVendida(dto.getQuantidadeVendida());
        produto.setComentariosAdmin(dto.getComentariosAdmin());
        produto.setAtualizadoEm(new java.util.Date());
        return repository.save(produto);
    }


    public void deletar(Long id) {
        Produto produto = buscarPorId(id);
        repository.delete(produto);
    }
}

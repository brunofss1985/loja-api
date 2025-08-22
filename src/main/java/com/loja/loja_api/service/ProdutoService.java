package com.loja.loja_api.service;

import com.loja.loja_api.dto.ProdutoDTO;
import com.loja.loja_api.model.Produto;
import com.loja.loja_api.repositories.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository repository;

    public Page<Produto> listarTodosPaginado(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findAll(pageable);
    }

    public Page<Produto> buscarPorCategoriaPaginado(String categoria, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findByCategoriaIgnoreCase(categoria, pageable);
    }

    public Produto buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
    }

    public Produto salvar(ProdutoDTO dto, MultipartFile imagem, List<MultipartFile> galeriaArquivos) {
        Produto produto = construirProduto(dto, imagem, galeriaArquivos);
        return repository.save(produto);
    }

    public Produto atualizar(Long id, ProdutoDTO dto, MultipartFile imagem, List<MultipartFile> galeriaArquivos) {
        Produto existente = buscarPorId(id);

        try {
            existente.setNome(dto.getNome());
            existente.setMarca(dto.getMarca());
            existente.setSlug(dto.getSlug());
            existente.setDescricao(dto.getDescricao());
            existente.setDescricaoCurta(dto.getDescricaoCurta());
            existente.setCategoria(dto.getCategoria());
            existente.setPeso(dto.getPeso());
            existente.setSabor(dto.getSabor());
            existente.setTamanhoPorcao(dto.getTamanhoPorcao());
            existente.setPreco(dto.getPreco());
            existente.setPrecoDesconto(dto.getPrecoDesconto());
            existente.setCusto(dto.getCusto());
            existente.setFornecedor(dto.getFornecedor());
            existente.setLucroEstimado(dto.getLucroEstimado());
            existente.setStatusAprovacao(dto.getStatusAprovacao());
            existente.setAtivo(dto.getAtivo());
            existente.setEstoque(dto.getEstoque());
            existente.setEstoqueMinimo(dto.getEstoqueMinimo());
            existente.setEstoqueMaximo(dto.getEstoqueMaximo());
            existente.setLocalizacaoFisica(dto.getLocalizacaoFisica());
            existente.setCodigoBarras(dto.getCodigoBarras());
            existente.setDimensoes(dto.getDimensoes());
            existente.setRestricoes(dto.getRestricoes());
            existente.setTabelaNutricional(dto.getTabelaNutricional());
            existente.setModoDeUso(dto.getModoDeUso());
            existente.setPalavrasChave(dto.getPalavrasChave());
            existente.setAvaliacaoMedia(dto.getAvaliacaoMedia());
            existente.setComentarios(dto.getComentarios());
            existente.setDataCadastro(dto.getDataCadastro());
            existente.setDataUltimaAtualizacao(dto.getDataUltimaAtualizacao());
            existente.setDataValidade(dto.getDataValidade());
            existente.setFornecedorId(dto.getFornecedorId());
            existente.setCnpjFornecedor(dto.getCnpjFornecedor());
            existente.setContatoFornecedor(dto.getContatoFornecedor());
            existente.setPrazoEntregaFornecedor(dto.getPrazoEntregaFornecedor());
            existente.setQuantidadeVendida(dto.getQuantidadeVendida());
            existente.setVendasMensais(dto.getVendasMensais());

            if (imagem != null && !imagem.isEmpty()) {
                existente.setImagem(imagem.getBytes());
                existente.setImagemMimeType(imagem.getContentType());
            }

            if (galeriaArquivos != null && !galeriaArquivos.isEmpty()) {
                List<byte[]> novaGaleria = new ArrayList<>();
                List<String> novosMimes = new ArrayList<>();

                for (MultipartFile file : galeriaArquivos) {
                    if (!file.isEmpty()) {
                        novaGaleria.add(file.getBytes());
                        novosMimes.add(file.getContentType());
                    }
                }

                existente.setGaleria(novaGaleria);
                existente.setGaleriaMimeTypes(novosMimes);
            }

            return repository.save(existente);

        } catch (IOException e) {
            throw new RuntimeException("Erro ao atualizar produto: " + e.getMessage());
        }
    }

    public void deletar(Long id) {
        Produto produto = buscarPorId(id);
        repository.delete(produto);
    }

    private Produto construirProduto(ProdutoDTO dto, MultipartFile imagem, List<MultipartFile> galeriaArquivos) {
        byte[] imagemBytes = null;
        String imagemMime = null;
        List<byte[]> galeria = new ArrayList<>();
        List<String> galeriaMimes = new ArrayList<>();

        try {
            if (imagem != null && !imagem.isEmpty()) {
                imagemBytes = imagem.getBytes();
                imagemMime = imagem.getContentType();
            }

            if (galeriaArquivos != null) {
                for (MultipartFile file : galeriaArquivos) {
                    if (!file.isEmpty()) {
                        galeria.add(file.getBytes());
                        galeriaMimes.add(file.getContentType());
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar imagens: " + e.getMessage());
        }

        return Produto.builder()
                .nome(dto.getNome())
                .marca(dto.getMarca())
                .slug(dto.getSlug())
                .descricao(dto.getDescricao())
                .descricaoCurta(dto.getDescricaoCurta())
                .categoria(dto.getCategoria())
                .peso(dto.getPeso())
                .sabor(dto.getSabor())
                .tamanhoPorcao(dto.getTamanhoPorcao())
                .preco(dto.getPreco())
                .precoDesconto(dto.getPrecoDesconto())
                .custo(dto.getCusto())
                .fornecedor(dto.getFornecedor())
                .lucroEstimado(dto.getLucroEstimado())
                .statusAprovacao(dto.getStatusAprovacao())
                .ativo(dto.getAtivo())
                .estoque(dto.getEstoque())
                .estoqueMinimo(dto.getEstoqueMinimo())
                .estoqueMaximo(dto.getEstoqueMaximo())
                .localizacaoFisica(dto.getLocalizacaoFisica())
                .codigoBarras(dto.getCodigoBarras())
                .dimensoes(dto.getDimensoes())
                .restricoes(dto.getRestricoes())
                .tabelaNutricional(dto.getTabelaNutricional())
                .modoDeUso(dto.getModoDeUso())
                .palavrasChave(dto.getPalavrasChave())
                .avaliacaoMedia(dto.getAvaliacaoMedia())
                .comentarios(dto.getComentarios())
                .dataCadastro(dto.getDataCadastro())
                .dataUltimaAtualizacao(dto.getDataUltimaAtualizacao())
                .dataValidade(dto.getDataValidade())
                .fornecedorId(dto.getFornecedorId())
                .cnpjFornecedor(dto.getCnpjFornecedor())
                .contatoFornecedor(dto.getContatoFornecedor())
                .prazoEntregaFornecedor(dto.getPrazoEntregaFornecedor())
                .quantidadeVendida(dto.getQuantidadeVendida())
                .vendasMensais(dto.getVendasMensais())
                .imagem(imagemBytes)
                .imagemMimeType(imagemMime)
                .galeria(galeria)
                .galeriaMimeTypes(galeriaMimes)
                .build();
    }
}
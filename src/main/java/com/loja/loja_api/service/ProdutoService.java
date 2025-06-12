package com.loja.loja_api.service;

import com.loja.loja_api.dto.ProdutoDTO;
import com.loja.loja_api.model.Produto;
import com.loja.loja_api.repositories.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
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

    public Produto salvar(ProdutoDTO dto, MultipartFile imagem, List<MultipartFile> galeriaArquivos) {
        Produto produto = construirProduto(dto, imagem, galeriaArquivos);
        return repository.save(produto);
    }

    public Produto atualizar(Long id, ProdutoDTO dto, MultipartFile imagem, List<MultipartFile> galeriaArquivos) {
        Produto existente = buscarPorId(id);
        Produto atualizado = construirProduto(dto, imagem, galeriaArquivos);
        atualizado.setId(existente.getId());
        return repository.save(atualizado);
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
                .imagem(imagemBytes)
                .imagemMimeType(imagemMime)
                .galeria(galeria)
                .galeriaMimeTypes(galeriaMimes)
                .build();
    }
}

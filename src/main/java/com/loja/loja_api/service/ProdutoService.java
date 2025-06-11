package com.loja.loja_api.service;

import com.loja.loja_api.dto.ProdutoDTO;
import com.loja.loja_api.model.Produto;
import com.loja.loja_api.repositories.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository repository;

    @Value("${caminho.uploads:uploads}")
    private String uploadDir;

    public List<Produto> listarTodos() {
        return repository.findAll();
    }

    public Produto buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
    }

    public Produto salvar(ProdutoDTO dto
            , MultipartFile imagem) {
        String imagemUrl = extrairOuGerarUrl(dto, imagem);

        Produto produto = Produto.builder()
                .nome(dto.getNome())
                .slug(dto.getSlug())
                .descricao(dto.getDescricao())
                .descricaoCurta(dto.getDescricaoCurta())
                .categoria(dto.getCategoria())
                .peso(dto.getPeso())
                .sabor(dto.getSabor())
                .tamanhoPorcao(dto.getTamanhoPorcao())
//                .tags(dto.getTags())
//                .ingredientes(dto.getIngredientes())
//                .galeria(dto.getGaleria())
//                .comentariosAdmin(dto.getComentariosAdmin())
//                .tabelaNutricional(dto.getTabelaNutricional())
//                .codigoBarras(dto.getCodigoBarras())
//                .sku(dto.getSku())
                .imagemUrl(imagemUrl)
//                .estoque(dto.getEstoque())
//                .qtdMinimaEstoque(dto.getQtdMinimaEstoque())
//                .quantidadeAvaliacoes(dto.getQuantidadeAvaliacoes())
//                .quantidadeVendida(dto.getQuantidadeVendida())
                .preco(dto.getPreco())
                .precoDesconto(dto.getPrecoDesconto())
                .custo(dto.getCusto())
                .fornecedor(dto.getFornecedor())
                .lucroEstimado(dto.getLucroEstimado())
//                .destaque(dto.getDestaque())
//                .novoLancamento(dto.getNovoLancamento())
//                .maisVendido(dto.getMaisVendido())
//                .promocaoAtiva(dto.getPromocaoAtiva())
//                .publicado(dto.getPublicado())
//                .ativo(dto.getAtivo())
                .statusAprovacao(dto.getStatusAprovacao())
//                .avaliacaoMedia(dto.getAvaliacaoMedia())
//                .criadoEm(new Date())
//                .atualizadoEm(new Date())
                .build();

        return repository.save(produto);
    }

    public Produto atualizar(Long id, ProdutoDTO dto, MultipartFile imagem
    ) {
        Produto produto = buscarPorId(id);
        String imagemUrl = extrairOuGerarUrl(dto, imagem);

        produto.setNome(dto.getNome());
        produto.setSlug(dto.getSlug());
        produto.setDescricao(dto.getDescricao());
        produto.setDescricaoCurta(dto.getDescricaoCurta());
        produto.setCategoria(dto.getCategoria());
        produto.setPeso(dto.getPeso());
        produto.setSabor(dto.getSabor());
        produto.setTamanhoPorcao(dto.getTamanhoPorcao());
//        produto.setTags(dto.getTags());
//        produto.setIngredientes(dto.getIngredientes());
//        produto.setGaleria(dto.getGaleria());
//        produto.setComentariosAdmin(dto.getComentariosAdmin());
//        produto.setTabelaNutricional(dto.getTabelaNutricional());
//        produto.setCodigoBarras(dto.getCodigoBarras());
//        produto.setSku(dto.getSku());
        produto.setPreco(dto.getPreco());
        produto.setPrecoDesconto(dto.getPrecoDesconto());
//        produto.setEstoque(dto.getEstoque());
//        produto.setQtdMinimaEstoque(dto.getQtdMinimaEstoque());
//        produto.setQuantidadeAvaliacoes(dto.getQuantidadeAvaliacoes());
//        produto.setQuantidadeVendida(dto.getQuantidadeVendida());
        produto.setCusto(dto.getCusto());
        produto.setFornecedor(dto.getFornecedor());
        produto.setLucroEstimado(dto.getLucroEstimado());
        produto.setImagemUrl(imagemUrl);
//        produto.setDestaque(dto.getDestaque());
//        produto.setNovoLancamento(dto.getNovoLancamento());
//        produto.setMaisVendido(dto.getMaisVendido());
//        produto.setPromocaoAtiva(dto.getPromocaoAtiva());
//        produto.setPublicado(dto.getPublicado());
//        produto.setAtivo(dto.getAtivo());
        produto.setStatusAprovacao(dto.getStatusAprovacao());
//        produto.setAvaliacaoMedia(dto.getAvaliacaoMedia());
//        produto.setAtualizadoEm(new Date());

        return repository.save(produto);
    }

    public void deletar(Long id) {
        Produto produto = buscarPorId(id);
        repository.delete(produto);
    }

    private String extrairOuGerarUrl(ProdutoDTO dto, MultipartFile imagem) {
        if (imagem != null && !imagem.isEmpty()) {
            try {
                String nomeImagem = UUID.randomUUID() + "_" + imagem.getOriginalFilename();
                Path diretorio = Paths.get(uploadDir);
                if (!Files.exists(diretorio)) {
                    Files.createDirectories(diretorio);
                }

                Path caminhoArquivo = diretorio.resolve(nomeImagem);
                Files.copy(imagem.getInputStream(), caminhoArquivo, StandardCopyOption.REPLACE_EXISTING);

                return "http://localhost:8080/imagens/" + nomeImagem;
            } catch (Exception e) {
                throw new RuntimeException("Erro ao salvar imagem: " + e.getMessage());
            }
        }
        return dto.getImagemUrl();
    }

    // Endpoint para servir imagem (caso não esteja em outro controller)
    @GetMapping("/imagens/{nome}")
    public ResponseEntity<Resource> getImagem(@PathVariable String nome) {
        try {
            Path caminho = Paths.get(uploadDir).resolve(nome).normalize();
            Resource recurso = new UrlResource(caminho.toUri());

            if (recurso.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaTypeFactory.getMediaType(recurso)
                                .orElse(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM))
                        .body(recurso);
            }
            return ResponseEntity.notFound().build();
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

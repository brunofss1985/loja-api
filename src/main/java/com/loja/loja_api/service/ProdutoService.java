package com.loja.loja_api.service;

import com.loja.loja_api.dto.ProdutoDTO;
import com.loja.loja_api.model.Produto;
import com.loja.loja_api.repositories.ProdutoRepository;
import com.loja.loja_api.repositories.ProdutoSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository repository;

    // ✅ Injetando o novo ImagemService
    @Autowired
    private ImagemService imagemService;

    @Transactional(readOnly = true)
    public Page<Produto> listarTodosPaginado(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Produto> buscarPorTermo(String termo, int page, int size, String sort) {
        Pageable pageable;
        if (sort != null && !sort.equalsIgnoreCase("relevance")) {
            String[] sortParams = sort.split(",");
            Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc") ?
                    Sort.Direction.DESC : Sort.Direction.ASC;
            Sort sortedBy = Sort.by(direction, sortParams[0]);
            pageable = PageRequest.of(page, size, sortedBy);
        } else {
            pageable = PageRequest.of(page, size);
        }
        return repository.findByTermo(termo, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Produto> buscarProdutosEmDestaque(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findByDestaqueAndAtivoTrue(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Produto> buscarProdutosComFiltros(
            List<String> categorias,
            List<String> marcas,
            List<String> objetivos,
            Double minPreco,
            Double maxPreco,
            int page,
            int size,
            String sort,
            Boolean destaque
    ) {
        Pageable pageable;
        if (sort != null && !sort.equalsIgnoreCase("relevance")) {
            String[] sortParams = sort.split(",");
            Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc") ?
                    Sort.Direction.DESC : Sort.Direction.ASC;
            Sort sortedBy = Sort.by(direction, sortParams[0]);
            pageable = PageRequest.of(page, size, sortedBy);
        } else {
            pageable = PageRequest.of(page, size);
        }

        List<String> categoriasFiltro = normalizeList(categorias);
        List<String> marcasFiltro = normalizeList(marcas);
        List<String> objetivosFiltro = normalizeList(objetivos);

        Specification<Produto> spec = ProdutoSpecification.comFiltros(
                categoriasFiltro,
                marcasFiltro,
                objetivosFiltro,
                minPreco,
                maxPreco,
                destaque
        );

        return repository.findAll(spec, pageable);
    }

    @Transactional(readOnly = true)
    public Produto buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
    }

    @Transactional
    public Produto salvar(ProdutoDTO dto, MultipartFile imagem, List<MultipartFile> galeriaArquivos) {
        // ✅ A lógica de processamento de imagens foi movida para o ImagemService
        Produto produto = Produto.builder()
                .nome(dto.getNome())
                .marca(dto.getMarca())
                .slug(dto.getSlug())
                .descricao(dto.getDescricao())
                .descricaoCurta(dto.getDescricaoCurta())
                .categorias(dto.getCategorias())
                .objetivos(dto.getObjetivos())
                .peso(dto.getPeso())
                .sabor(dto.getSabor())
                .tamanhoPorcao(dto.getTamanhoPorcao())
                .preco(dto.getPreco())
                .precoDesconto(dto.getPrecoDesconto())
                .porcentagemDesconto(dto.getPorcentagemDesconto())
                .custo(dto.getCusto())
                .fornecedor(dto.getFornecedor())
                .lucroEstimado(dto.getLucroEstimado())
                .statusAprovacao(dto.getStatusAprovacao())
                .ativo(dto.getAtivo())
                .destaque(dto.getDestaque())
                .disponibilidade(dto.getDisponibilidade())
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
                .imagem(imagemService.processarImagem(imagem))
                .imagemMimeType(imagemService.getImagemMimeType(imagem))
                .galeria(imagemService.processarGaleria(galeriaArquivos))
                .galeriaMimeTypes(imagemService.getGaleriaMimeTypes(galeriaArquivos))
                .build();
        return repository.save(produto);
    }

    @Transactional
    public Produto atualizar(Long id, ProdutoDTO dto, MultipartFile imagem, List<MultipartFile> galeriaArquivos) {
        Produto existente = buscarPorId(id);

        try {
            existente.setNome(dto.getNome());
            existente.setMarca(dto.getMarca());
            existente.setSlug(dto.getSlug());
            existente.setDescricao(dto.getDescricao());
            existente.setDescricaoCurta(dto.getDescricaoCurta());
            existente.setCategorias(dto.getCategorias());
            existente.setPeso(dto.getPeso());
            existente.setSabor(dto.getSabor());
            existente.setTamanhoPorcao(dto.getTamanhoPorcao());
            existente.setPreco(dto.getPreco());
            existente.setPrecoDesconto(dto.getPrecoDesconto());
            existente.setPorcentagemDesconto(dto.getPorcentagemDesconto());
            existente.setCusto(dto.getCusto());
            existente.setFornecedor(dto.getFornecedor());
            existente.setLucroEstimado(dto.getLucroEstimado());
            existente.setStatusAprovacao(dto.getStatusAprovacao());
            existente.setAtivo(dto.getAtivo());
            existente.setDestaque(dto.getDestaque());
            existente.setObjetivos(dto.getObjetivos());
            existente.setDisponibilidade(dto.getDisponibilidade());
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

            // ✅ Lógica de processamento de imagens movida para o ImagemService
            if (imagem != null && !imagem.isEmpty()) {
                existente.setImagem(imagemService.processarImagem(imagem));
                existente.setImagemMimeType(imagemService.getImagemMimeType(imagem));
            }
            if (galeriaArquivos != null && !galeriaArquivos.isEmpty()) {
                existente.setGaleria(imagemService.processarGaleria(galeriaArquivos));
                existente.setGaleriaMimeTypes(imagemService.getGaleriaMimeTypes(galeriaArquivos));
            }
            return repository.save(existente);

        } catch (RuntimeException e) {
            throw new RuntimeException("Erro ao atualizar produto: " + e.getMessage());
        }
    }

    @Transactional
    public void deletar(Long id) {
        Produto produto = buscarPorId(id);
        repository.delete(produto);
    }

    private List<String> normalizeList(List<String> raw) {
        if (raw == null || raw.isEmpty()) return null;
        List<String> out = new ArrayList<>();
        for (String s : raw) {
            if (s == null) continue;
            for (String p : s.split(",")) {
                String t = p.trim();
                if (!t.isEmpty()) out.add(t);
            }
        }
        return out.isEmpty() ? null : out;
    }
}
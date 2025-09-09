package com.loja.loja_api.services;

import com.loja.loja_api.dto.ProdutoDTO;
import com.loja.loja_api.models.Produto;
import com.loja.loja_api.repositories.ProdutoRepository;
import com.loja.loja_api.repositories.ProdutoSpecification;
import com.loja.loja_api.util.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository repository;

    @Autowired
    private ImagemService imagemService;

    private Pageable buildPageable(int page, int size, String sort) {
        if (sort != null && !sort.equalsIgnoreCase("relevance")) {
            String[] sortParams = sort.split(",");
            Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc") ?
                    Sort.Direction.DESC : Sort.Direction.ASC;
            return PageRequest.of(page, size, Sort.by(direction, sortParams[0]));
        }
        return PageRequest.of(page, size);
    }

    @Transactional(readOnly = true)
    public Page<ProdutoDTO> buscarPorTermo(String termo, int page, int size, String sort) {
        Pageable pageable = buildPageable(page, size, sort);
        return repository.findByTermo(termo, pageable).map(ProdutoDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<ProdutoDTO> buscarProdutosEmDestaque(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findByDestaqueAndAtivoTrue(pageable).map(ProdutoDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<ProdutoDTO> buscarProdutosComFiltros(
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
        Pageable pageable = buildPageable(page, size, sort);

        Specification<Produto> spec = ProdutoSpecification.comFiltros(
                ListUtils.normalizeList(categorias),
                ListUtils.normalizeList(marcas),
                ListUtils.normalizeList(objetivos),
                minPreco,
                maxPreco,
                destaque
        );

        return repository.findAll(spec, pageable).map(ProdutoDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public ProdutoDTO buscarPorId(Long id) {
        Produto produto = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
        return ProdutoDTO.fromEntity(produto);
    }

    @Transactional
    public ProdutoDTO salvar(ProdutoDTO dto, MultipartFile imagem, List<MultipartFile> galeriaArquivos) {
        Produto produto = Produto.builder()
                .nome(dto.getNome())
                .marca(dto.getMarca())
                .slug(dto.getSlug())
                .descricao(dto.getDescricao())
                .descricaoCurta(dto.getDescricaoCurta())
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
                .tabelaNutricional(dto.getTabelaNutricional())
                .modoDeUso(dto.getModoDeUso())
                .palavrasChave(dto.getPalavrasChave())
                .avaliacaoMedia(dto.getAvaliacaoMedia())
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

        Produto salvo = repository.save(produto);
        return ProdutoDTO.fromEntity(salvo);
    }

    @Transactional
    public ProdutoDTO atualizar(Long id, ProdutoDTO dto, MultipartFile imagem, List<MultipartFile> galeriaArquivos) {
        Produto existente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        existente.setNome(dto.getNome());
        existente.setMarca(dto.getMarca());
        existente.setSlug(dto.getSlug());
        existente.setDescricao(dto.getDescricao());
        existente.setDescricaoCurta(dto.getDescricaoCurta());
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
        existente.setDisponibilidade(dto.getDisponibilidade());
        existente.setEstoque(dto.getEstoque());
        existente.setEstoqueMinimo(dto.getEstoqueMinimo());
        existente.setEstoqueMaximo(dto.getEstoqueMaximo());
        existente.setLocalizacaoFisica(dto.getLocalizacaoFisica());
        existente.setCodigoBarras(dto.getCodigoBarras());
        existente.setDimensoes(dto.getDimensoes());
        existente.setTabelaNutricional(dto.getTabelaNutricional());
        existente.setModoDeUso(dto.getModoDeUso());
        existente.setPalavrasChave(dto.getPalavrasChave());
        existente.setAvaliacaoMedia(dto.getAvaliacaoMedia());
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
            existente.setImagem(imagemService.processarImagem(imagem));
            existente.setImagemMimeType(imagemService.getImagemMimeType(imagem));
        }
        if (galeriaArquivos != null && !galeriaArquivos.isEmpty()) {
            existente.setGaleria(imagemService.processarGaleria(galeriaArquivos));
            existente.setGaleriaMimeTypes(imagemService.getGaleriaMimeTypes(galeriaArquivos));
        }

        Produto atualizado = repository.save(existente);
        return ProdutoDTO.fromEntity(atualizado);
    }

    @Transactional
    public void deletar(Long id) {
        Produto produto = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
        repository.delete(produto);
    }
}

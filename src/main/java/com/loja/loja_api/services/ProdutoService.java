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

    @Transactional(readOnly = true)
    public Page<Produto> listarTodosPaginado(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Produto> buscarPorTermo(String termo, int page, int size, String sort) {
        Pageable pageable = getPageable(page, size, sort);
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
        Pageable pageable = getPageable(page, size, sort);

        Specification<Produto> spec = ProdutoSpecification.comFiltros(
                ListUtils.normalizeList(categorias),
                ListUtils.normalizeList(marcas),
                ListUtils.normalizeList(objetivos),
                minPreco,
                maxPreco,
                destaque
        );

        return repository.findAll(spec, pageable);
    }

    private Pageable getPageable(int page, int size, String sort) {
        if (sort != null && !sort.equalsIgnoreCase("relevance")) {
            String[] sortParams = sort.split(",");
            Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")
                    ? Sort.Direction.DESC : Sort.Direction.ASC;
            Sort sortedBy = Sort.by(direction, sortParams[0]);
            return PageRequest.of(page, size, sortedBy);
        } else {
            return PageRequest.of(page, size);
        }
    }

    @Transactional(readOnly = true)
    public Produto buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
    }

    @Transactional
    public Produto salvar(ProdutoDTO dto, MultipartFile imagem, List<MultipartFile> galeriaArquivos) {
        Produto produto = buildProdutoFromDTO(dto);

        if (imagem != null && !imagem.isEmpty()) {
            produto.setImagem(imagemService.processarImagem(imagem));
            produto.setImagemMimeType(imagemService.getImagemMimeType(imagem));
        }

        if (galeriaArquivos != null && !galeriaArquivos.isEmpty()) {
            produto.setGaleria(imagemService.processarGaleria(galeriaArquivos));
            produto.setGaleriaMimeTypes(imagemService.getGaleriaMimeTypes(galeriaArquivos));
        }

        return repository.save(produto);
    }

    @Transactional
    public Produto atualizar(Long id, ProdutoDTO dto, MultipartFile imagem, List<MultipartFile> galeriaArquivos) {
        Produto existente = buscarPorId(id);
        Produto atualizado = buildProdutoFromDTO(dto);
        atualizado.setId(existente.getId());

        if (imagem != null && !imagem.isEmpty()) {
            atualizado.setImagem(imagemService.processarImagem(imagem));
            atualizado.setImagemMimeType(imagemService.getImagemMimeType(imagem));
        } else {
            atualizado.setImagem(existente.getImagem());
            atualizado.setImagemMimeType(existente.getImagemMimeType());
        }

        if (galeriaArquivos != null && !galeriaArquivos.isEmpty()) {
            atualizado.setGaleria(imagemService.processarGaleria(galeriaArquivos));
            atualizado.setGaleriaMimeTypes(imagemService.getGaleriaMimeTypes(galeriaArquivos));
        } else {
            atualizado.setGaleria(existente.getGaleria());
            atualizado.setGaleriaMimeTypes(existente.getGaleriaMimeTypes());
        }

        return repository.save(atualizado);
    }

    private Produto buildProdutoFromDTO(ProdutoDTO dto) {
        return Produto.builder()
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
                // ❌ não setamos mais estoque, ele vem dos lotes
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
                .build();
    }

    @Transactional
    public void deletar(Long id) {
        Produto produto = buscarPorId(id);
        repository.delete(produto);
    }
}

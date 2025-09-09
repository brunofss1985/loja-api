package com.loja.loja_api.dto;

import com.loja.loja_api.models.Dimensoes;
import com.loja.loja_api.models.Produto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProdutoDTO {

    // Dados principais
    private String nome;
    private String marca;
    private String slug;
    private String descricao;
    private String descricaoCurta;
    private List<String> categorias;
    private List<String> objetivos;
    private String sabor;
    private String tamanhoPorcao;
    private String fornecedor;
    private String statusAprovacao;
    private String disponibilidade;

    // Valores
    private Double peso;
    private Double preco;
    private Double precoDesconto;
    private String porcentagemDesconto;
    private Double custo;
    private Double lucroEstimado;
    private Boolean ativo;
    private Boolean destaque;

    // Estoque e logística
    private Integer estoque;
    private Integer estoqueMinimo;
    private Integer estoqueMaximo;
    private String localizacaoFisica;
    private String codigoBarras;
    private Dimensoes dimensoes;
    private List<String> restricoes;

    // Nutrição e uso
    private String tabelaNutricional;
    private String modoDeUso;

    // SEO e avaliações
    private List<String> palavrasChave;
    private Double avaliacaoMedia;
    private List<String> comentarios;

    // Datas
    private LocalDate dataCadastro;
    private LocalDate dataUltimaAtualizacao;
    private LocalDate dataValidade;

    // Fornecedor extra
    private Long fornecedorId;
    private String cnpjFornecedor;
    private String contatoFornecedor;
    private String prazoEntregaFornecedor;

    // Vendas
    private Integer quantidadeVendida;
    private List<Integer> vendasMensais;

    // 🔹 Construtor auxiliar para mapear de Produto -> ProdutoDTO
    public static ProdutoDTO fromEntity(Produto produto) {
        return ProdutoDTO.builder()
                .nome(produto.getNome())
                .marca(produto.getMarca())
                .slug(produto.getSlug())
                .descricao(produto.getDescricao())
                .descricaoCurta(produto.getDescricaoCurta())
                .categorias(produto.getCategorias())   // já é List<String>
                .objetivos(produto.getObjetivos())     // já é List<String>
                .sabor(produto.getSabor())
                .tamanhoPorcao(produto.getTamanhoPorcao())
                .fornecedor(produto.getFornecedor())
                .statusAprovacao(produto.getStatusAprovacao())
                .disponibilidade(produto.getDisponibilidade())
                .peso(produto.getPeso())
                .preco(produto.getPreco())
                .precoDesconto(produto.getPrecoDesconto())
                .porcentagemDesconto(produto.getPorcentagemDesconto())
                .custo(produto.getCusto())
                .lucroEstimado(produto.getLucroEstimado())
                .ativo(produto.getAtivo())
                .destaque(produto.getDestaque())
                .estoque(produto.getEstoque())
                .estoqueMinimo(produto.getEstoqueMinimo())
                .estoqueMaximo(produto.getEstoqueMaximo())
                .localizacaoFisica(produto.getLocalizacaoFisica())
                .codigoBarras(produto.getCodigoBarras())
                .dimensoes(produto.getDimensoes())
                .restricoes(produto.getRestricoes())   // já é List<String>
                .tabelaNutricional(produto.getTabelaNutricional())
                .modoDeUso(produto.getModoDeUso())
                .palavrasChave(produto.getPalavrasChave()) // já é List<String>
                .avaliacaoMedia(produto.getAvaliacaoMedia())
                .comentarios(produto.getComentarios()) // já é List<String>
                .dataCadastro(produto.getDataCadastro())
                .dataUltimaAtualizacao(produto.getDataUltimaAtualizacao())
                .dataValidade(produto.getDataValidade())
                .fornecedorId(produto.getFornecedorId())
                .cnpjFornecedor(produto.getCnpjFornecedor())
                .contatoFornecedor(produto.getContatoFornecedor())
                .prazoEntregaFornecedor(produto.getPrazoEntregaFornecedor())
                .quantidadeVendida(produto.getQuantidadeVendida())
                .vendasMensais(produto.getVendasMensais())
                .build();
    }
}

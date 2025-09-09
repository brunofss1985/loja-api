package com.loja.loja_api.dto;

import com.loja.loja_api.models.Dimensoes;
import com.loja.loja_api.models.Produto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProdutoDTO {

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

    private Double peso;
    private Double preco;
    private Double precoDesconto;
    private String porcentagemDesconto;
    private Double custo;
    private Double lucroEstimado;
    private Boolean ativo;
    private Boolean destaque;

    private Integer estoque;
    private Integer estoqueMinimo;
    private Integer estoqueMaximo;
    private String localizacaoFisica;
    private String codigoBarras;
    private Dimensoes dimensoes;
    private List<String> restricoes;

    private String tabelaNutricional;
    private String modoDeUso;

    private List<String> palavrasChave;
    private Double avaliacaoMedia;
    private List<String> comentarios;

    private LocalDate dataCadastro;
    private LocalDate dataUltimaAtualizacao;
    private LocalDate dataValidade;

    private Long fornecedorId;
    private String cnpjFornecedor;
    private String contatoFornecedor;
    private String prazoEntregaFornecedor;

    private Integer quantidadeVendida;
    private List<Integer> vendasMensais;

    public static ProdutoDTO fromEntity(Produto p) {
        return ProdutoDTO.builder()
                .nome(p.getNome())
                .marca(p.getMarca())
                .slug(p.getSlug())
                .descricao(p.getDescricao())
                .descricaoCurta(p.getDescricaoCurta())
                // CÓPIA DEFENSIVA — evita LazyInitialization e tipos do Hibernate no DTO
                .categorias(copyList(p.getCategorias()))
                .objetivos(copyList(p.getObjetivos()))
                .sabor(p.getSabor())
                .tamanhoPorcao(p.getTamanhoPorcao())
                .fornecedor(p.getFornecedor())
                .statusAprovacao(p.getStatusAprovacao())
                .disponibilidade(p.getDisponibilidade())
                .peso(p.getPeso())
                .preco(p.getPreco())
                .precoDesconto(p.getPrecoDesconto())
                .porcentagemDesconto(p.getPorcentagemDesconto())
                .custo(p.getCusto())
                .lucroEstimado(p.getLucroEstimado())
                .ativo(p.getAtivo())
                .destaque(p.getDestaque())
                .estoque(p.getEstoque())
                .estoqueMinimo(p.getEstoqueMinimo())
                .estoqueMaximo(p.getEstoqueMaximo())
                .localizacaoFisica(p.getLocalizacaoFisica())
                .codigoBarras(p.getCodigoBarras())
                .dimensoes(p.getDimensoes()) // é embeddable simples
                .restricoes(copyList(p.getRestricoes()))
                .tabelaNutricional(p.getTabelaNutricional())
                .modoDeUso(p.getModoDeUso())
                .palavrasChave(copyList(p.getPalavrasChave()))
                .avaliacaoMedia(p.getAvaliacaoMedia())
                .comentarios(copyList(p.getComentarios()))
                .dataCadastro(p.getDataCadastro())
                .dataUltimaAtualizacao(p.getDataUltimaAtualizacao())
                .dataValidade(p.getDataValidade())
                .fornecedorId(p.getFornecedorId())
                .cnpjFornecedor(p.getCnpjFornecedor())
                .contatoFornecedor(p.getContatoFornecedor())
                .prazoEntregaFornecedor(p.getPrazoEntregaFornecedor())
                .quantidadeVendida(p.getQuantidadeVendida())
                .vendasMensais(copyList(p.getVendasMensais()))
                .build();
    }

    private static <T> List<T> copyList(List<T> src) {
        if (src == null) return null;
        // cria uma lista “pura”, inicializando o proxy ao iterar
        List<T> out = new ArrayList<>(src.size());
        for (T t : src) out.add(t);
        return out;
    }
}

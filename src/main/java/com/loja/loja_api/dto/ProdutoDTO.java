package com.loja.loja_api.dto;

import com.loja.loja_api.model.Dimensoes;
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
    private String categoria;
    private String sabor;
    private String tamanhoPorcao;
    private String fornecedor;
    private String statusAprovacao;

    // Valores
    private Double peso;
    private Double preco;
    private Double precoDesconto;
    private Double custo;
    private Double lucroEstimado;
    private Boolean ativo;

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
}

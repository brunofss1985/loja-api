package com.loja.loja_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProdutoDTO {
    private String nome;
    private String descricao;
    private String categoria;
    private String slug;
    private String descricaoCurta;
    private String sabor;
    private String tamanhoPorcao;
    private String fornecedor;
    private String statusAprovacao;

    private Double peso;
    private Double preco;
    private Double precoDesconto;
    private Double custo;
    private Double lucroEstimado;

//    private String tabelaNutricional;
//    private String sku;
//    private String codigoBarras;
    private String imagemUrl;

//    private Integer qtdMinimaEstoque;
//    private Integer estoque;
//    private Integer quantidadeAvaliacoes;
//    private Integer quantidadeVendida;

//    private Boolean destaque;
//    private Boolean novoLancamento;
//    private Boolean maisVendido;
//    private Boolean promocaoAtiva;
//    private Boolean publicado;
//    private Boolean ativo;

//    private Double avaliacaoMedia;

//    private List<String> tags;
//    private List<String> ingredientes;
//    private List<String> galeria;
//    private List<String> comentariosAdmin;
}

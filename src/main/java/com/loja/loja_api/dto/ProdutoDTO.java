package com.loja.loja_api.dto;

import jakarta.persistence.ElementCollection;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProdutoDTO {
    private String nome;
    private String slug;
    private String descricao;
    private String descricaoCurta;
    private String categoria;

    private Double peso;
    private String sabor;
    private String tamanhoPorcao;

    private String tabelaNutricional;
    private Double preco;
    private Double precoDesconto;
    private Integer estoque;
    private Integer qtdMinimaEstoque;
    private Double custo;
    private String fornecedor;
    private Double lucroEstimado;
    private String sku;
    private String codigoBarras;
    private String imagemUrl;

    private Boolean destaque;
    private Boolean novoLancamento;
    private Boolean maisVendido;
    private Boolean promocaoAtiva;
    private String statusAprovacao;
    private Boolean publicado;
    private Double avaliacaoMedia;
    private Integer quantidadeAvaliacoes;
    private Boolean ativo;
    private Integer quantidadeVendida;

    @ElementCollection
    private List<String> tags;

    @ElementCollection
    private List<String> ingredientes;

    @ElementCollection
    private List<String> galeria;

    @ElementCollection
    private List<String> comentariosAdmin;
}

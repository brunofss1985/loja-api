package com.loja.loja_api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String slug;

    @Column(length = 1000)
    private String descricao;

    @Column(length = 500)
    private String descricaoCurta;
    private String categoria;
    private String sabor;
    private String tamanhoPorcao;
    private String statusAprovacao;
    private String fornecedor;

    private Double peso;
    private Double preco;
    private Double precoDesconto;
    private Double custo;
    private Double lucroEstimado;

//    @Column(columnDefinition = "TEXT")
//    private String tabelaNutricional; // JSON serializado como string
//    private String sku;
//    private String codigoBarras;
    private String imagemUrl;


//    private Boolean destaque;
//    private Boolean novoLancamento;
//    private Boolean maisVendido;
//    private Boolean promocaoAtiva;
//    private Boolean publicado;
//    private Boolean ativo;

//    private Double avaliacaoMedia;

//    private Integer estoque;
//    private Integer qtdMinimaEstoque;
//    private Integer quantidadeVendida;
//    private Integer quantidadeAvaliacoes;

//    private Date dataExpiracao;
//    private Date ultimaCompra;
//    private Date criadoEm;
//    private Date atualizadoEm;

//    @ElementCollection
//    private List<String> tags;
//
//    @ElementCollection
//    private List<String> ingredientes;
//
//    @ElementCollection
//    private List<String> galeria;
//
//    @ElementCollection
//    private List<String> comentariosAdmin;
}

package com.loja.loja_api.model;

import jakarta.persistence.*;
import lombok.*;
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

    private Double peso;
    private String sabor;
    private String tamanhoPorcao;

    @Column(columnDefinition = "TEXT")
    private String tabelaNutricional; // JSON como string
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
    private Date dataExpiracao;
    private Date ultimaCompra;
    private Integer quantidadeVendida;
    private String statusAprovacao;
    private Boolean publicado;
    private Double avaliacaoMedia;
    private Integer quantidadeAvaliacoes;
    private Boolean ativo;
    private Date criadoEm;
    private Date atualizadoEm;
    @ElementCollection
    private List<String> tags;
    @ElementCollection
    private List<String> ingredientes;
    @ElementCollection
    private List<String> galeria;
    @ElementCollection
    private List<String> comentariosAdmin;
}

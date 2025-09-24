package com.loja.loja_api.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
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

    // Dados gerais
    private String nome;
    private String marca;
    private String slug;

    @Column(length = 1000)
    private String descricao;

    @Column(length = 500)
    private String descricaoCurta;

    @ElementCollection
    @CollectionTable(name = "produto_categorias", joinColumns = @JoinColumn(name = "produto_id"))
    @Column(name = "categoria")
    private List<String> categorias;

    @ElementCollection
    @CollectionTable(name = "produto_objetivos", joinColumns = @JoinColumn(name = "produto_id"))
    @Column(name = "objetivo")
    private List<String> objetivos;

    private String sabor;
    private String tamanhoPorcao;
    private String statusAprovacao;
    private String disponibilidade;

    private Double peso;
    private Double preco;
    private Double precoDesconto;
    private String porcentagemDesconto;
    private Boolean ativo;
    private Boolean destaque;

    private Integer estoqueMinimo;
    private Integer estoqueMaximo;

    @Embedded
    private Dimensoes dimensoes;

    @ElementCollection
    @CollectionTable(name = "produto_restricoes", joinColumns = @JoinColumn(name = "produto_id"))
    @Column(name = "restricao")
    private List<String> restricoes;

    @Lob
    private String tabelaNutricional;
    private String modoDeUso;

    @ElementCollection
    @CollectionTable(name = "produto_palavras_chave", joinColumns = @JoinColumn(name = "produto_id"))
    @Column(name = "palavra")
    private List<String> palavrasChave;

    private Double avaliacaoMedia;

    @ElementCollection
    @CollectionTable(name = "produto_comentarios", joinColumns = @JoinColumn(name = "produto_id"))
    @Column(name = "comentario")
    private List<String> comentarios;

    private Integer quantidadeVendida;

    @ElementCollection
    @CollectionTable(name = "produto_vendas_mensais", joinColumns = @JoinColumn(name = "produto_id"))
    @Column(name = "valor")
    private List<Integer> vendasMensais;

    @Lob
    private byte[] imagem;

    private String imagemMimeType;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "produto_galeria", joinColumns = @JoinColumn(name = "produto_id"))
    @Column(name = "imagem")
    private List<byte[]> galeria;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "produto_galeria_mime", joinColumns = @JoinColumn(name = "produto_id"))
    @Column(name = "mime_type")
    private List<String> galeriaMimeTypes;

    @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Lote> lotes;
}

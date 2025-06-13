package com.loja.loja_api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private Boolean ativo;

    // Estoque e logística
    private Integer estoque;
    private Integer estoqueMinimo;
    private Integer estoqueMaximo;
    private String localizacaoFisica;
    private String codigoBarras;

    @Embedded
    private Dimensoes dimensoes;

    @ElementCollection
    @CollectionTable(name = "produto_restricoes", joinColumns = @JoinColumn(name = "produto_id"))
    @Column(name = "restricao")
    private List<String> restricoes;

    // Nutrição e uso
    @Lob
    private String tabelaNutricional;

    private String modoDeUso;

    // SEO e avaliações
    @ElementCollection
    @CollectionTable(name = "produto_palavras_chave", joinColumns = @JoinColumn(name = "produto_id"))
    @Column(name = "palavra")
    private List<String> palavrasChave;

    private Double avaliacaoMedia;

    @ElementCollection
    @CollectionTable(name = "produto_comentarios", joinColumns = @JoinColumn(name = "produto_id"))
    @Column(name = "comentario")
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

    @ElementCollection
    @CollectionTable(name = "produto_vendas_mensais", joinColumns = @JoinColumn(name = "produto_id"))
    @Column(name = "valor")
    private List<Integer> vendasMensais;

    // Imagem principal
    @Lob
    private byte[] imagem;

    private String imagemMimeType;

    // Galeria
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "produto_galeria", joinColumns = @JoinColumn(name = "produto_id"))
    @Column(name = "imagem")
    private List<byte[]> galeria;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "produto_galeria_mime", joinColumns = @JoinColumn(name = "produto_id"))
    @Column(name = "mime_type")
    private List<String> galeriaMimeTypes;
}

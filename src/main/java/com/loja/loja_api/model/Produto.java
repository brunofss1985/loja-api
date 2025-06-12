package com.loja.loja_api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    // Substituindo imagemUrl por conteúdo binário
    @Lob
    private byte[] imagem;

    private String imagemMimeType;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "produto_galeria")
    @Column(name = "imagem")
    private List<byte[]> galeria;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "produto_galeria_mime")
    @Column(name = "mime_type")
    private List<String> galeriaMimeTypes;
}

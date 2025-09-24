package com.loja.loja_api.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "lotes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String codigo;

    @Transient
    private Integer quantidade;

    @Column(nullable = false)
    private LocalDate dataValidade;

    @Column(nullable = false)
    private String fornecedor;

    @Column(nullable = false)
    private Double custoPorUnidade;

    @Column(nullable = false)
    private String localArmazenamento;

    @Column(nullable = false)
    private String statusLote;

    @Column(nullable = false)
    private LocalDate dataRecebimento;

    @Column(nullable = false)
    private Double valorVendaSugerido;

    @Column(nullable = false)
    private String notaFiscalEntrada;

    @Column(nullable = false)
    private String contatoVendedor;

    // ✅ Novos campos
    private Double custoTotalLote;
    private Double lucroTotalEstimado;
    private Double lucroEstimadoPorUnidade;
    private String codigoBarras;
    private String cnpjFornecedor;
    private LocalDate dataCadastro;
    private LocalDate dataAtualizacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", nullable = false)
    @JsonIgnore
    private Produto produto;
}

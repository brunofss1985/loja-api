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

    @Column(nullable = false)
    private Integer quantidade;

    @Column(nullable = false)
    private LocalDate dataValidade;

    // Novos campos
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

    // FK para Produto (ignorado no JSON para evitar LOB serialization)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", nullable = false)
    @JsonIgnore
    private Produto produto;
}

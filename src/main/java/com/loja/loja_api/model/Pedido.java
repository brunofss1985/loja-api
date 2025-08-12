package com.loja.loja_api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String cpf;
    private String email;
    private Double valor;
    private String tipoPagamento;
    private String linkPagamento;
    private String status = "PENDING";

    @Column(name = "payment_id")
    private String paymentId;


}

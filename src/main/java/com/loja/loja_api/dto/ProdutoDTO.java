package com.loja.loja_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}

package com.loja.loja_api.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProdutoDTO {
    private String nome;
    private String marca;
    private String categoria;
    private String tipo;
    private String descricao;
}

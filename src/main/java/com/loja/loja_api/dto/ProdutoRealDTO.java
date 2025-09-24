package com.loja.loja_api.dto;

import com.loja.loja_api.models.ProdutoReal;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProdutoRealDTO {

    private Long id;
    private Integer quantidade;
    private String codigoBarras;
    private String localizacaoFisica;
    private LocalDate dataValidade;

    private Long loteId;
    private Long produtoId;

    public static ProdutoRealDTO fromEntity(ProdutoReal pr) {
        return ProdutoRealDTO.builder()
                .id(pr.getId())
                .quantidade(pr.getQuantidade())
                .codigoBarras(pr.getCodigoBarras())
                .localizacaoFisica(pr.getLocalizacaoFisica())
                .dataValidade(pr.getDataValidade())
                .loteId(pr.getLote() != null ? pr.getLote().getId() : null)
                .produtoId(pr.getProduto() != null ? pr.getProduto().getId() : null)
                .build();
    }
}

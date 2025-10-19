package com.loja.loja_api.dto;

import com.loja.loja_api.models.ProdutoVendido;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProdutoVendidoDTO {
    private Long id;
    private Long produtoId;
    private Long orderId;
    private String produtoNome;
    private String customerNome;
    private String customerEmail;
    private String codigoBarras;
    private String loteCodigo;
    private Instant dataVenda;
    private BigDecimal valorVenda;

    public static ProdutoVendidoDTO fromEntity(ProdutoVendido pv) {
        return ProdutoVendidoDTO.builder()
                .id(pv.getId())
                .produtoId(pv.getProduto() != null ? pv.getProduto().getId() : null)
                .orderId(pv.getOrder() != null ? pv.getOrder().getId() : null)
                .produtoNome(pv.getProdutoNome())
                .customerNome(pv.getCustomerNome())
                .customerEmail(pv.getCustomerEmail())
                .codigoBarras(pv.getCodigoBarras())
                .loteCodigo(pv.getLoteCodigo())
                .dataVenda(pv.getDataVenda())
                .valorVenda(pv.getValorVenda())
                .build();
    }
}

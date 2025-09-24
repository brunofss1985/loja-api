package com.loja.loja_api.dto;

import com.loja.loja_api.models.Lote;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoteDTO {

    private Long id;
    private String codigo;
    private Long produtoId;
    private String produtoNome;
    private LocalDate dataValidade;

    private String fornecedor;
    private Double custoPorUnidade;
    private String localArmazenamento;
    private String statusLote;
    private LocalDate dataRecebimento;
    private Double valorVendaSugerido;
    private String notaFiscalEntrada;
    private String contatoVendedor;

    private Integer quantidadeTotal;

    // ✅ Novos campos
    private Double custoTotalLote;
    private Double lucroTotalEstimado;
    private Double lucroEstimadoPorUnidade;
    private String codigoBarras;
    private String cnpjFornecedor;
    private LocalDate dataCadastro;
    private LocalDate dataAtualizacao;

    public static LoteDTO fromEntity(Lote lote) {
        return LoteDTO.builder()
                .id(lote.getId())
                .codigo(lote.getCodigo())
                .produtoId(lote.getProduto() != null ? lote.getProduto().getId() : null)
                .produtoNome(lote.getProduto() != null ? lote.getProduto().getNome() : null)
                .dataValidade(lote.getDataValidade())
                .fornecedor(lote.getFornecedor())
                .custoPorUnidade(lote.getCustoPorUnidade())
                .localArmazenamento(lote.getLocalArmazenamento())
                .statusLote(lote.getStatusLote())
                .dataRecebimento(lote.getDataRecebimento())
                .valorVendaSugerido(lote.getValorVendaSugerido())
                .notaFiscalEntrada(lote.getNotaFiscalEntrada())
                .contatoVendedor(lote.getContatoVendedor())
                .quantidadeTotal(lote.getQuantidade())

                // ✅ Novos campos
                .custoTotalLote(lote.getCustoTotalLote())
                .lucroTotalEstimado(lote.getLucroTotalEstimado())
                .lucroEstimadoPorUnidade(lote.getLucroEstimadoPorUnidade())
                .codigoBarras(lote.getCodigoBarras())
                .cnpjFornecedor(lote.getCnpjFornecedor())
                .dataCadastro(lote.getDataCadastro())
                .dataAtualizacao(lote.getDataAtualizacao())
                .build();
    }
}

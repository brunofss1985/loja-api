package com.loja.loja_api.model;

import lombok.Data;

@Data
public class PagamentoResponse {
    private String boletoUrl;
    private String qrCode;
    private String qrCodeBase64;
}

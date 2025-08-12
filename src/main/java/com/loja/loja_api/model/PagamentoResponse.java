package com.loja.loja_api.model;

public class PagamentoResponse {

    private String boletoUrl;
    private String qrCode;
    private String qrCodeBase64;

    public String getBoletoUrl() {
        return boletoUrl;
    }

    public void setBoletoUrl(String boletoUrl) {
        this.boletoUrl = boletoUrl;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getQrCodeBase64() {
        return qrCodeBase64;
    }

    public void setQrCodeBase64(String qrCodeBase64) {
        this.qrCodeBase64 = qrCodeBase64;
    }
}

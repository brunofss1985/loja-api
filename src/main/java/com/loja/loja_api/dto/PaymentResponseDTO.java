package com.loja.loja_api.dto;

import com.loja.loja_api.enums.PaymentStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentResponseDTO {

    private Long orderId;
    private Long paymentId;
    private PaymentStatus status;
    private String message;
    private String qrCode;
    private String qrCodeBase64;
    private String boletoUrl;

}
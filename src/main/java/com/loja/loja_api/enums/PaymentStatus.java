package com.loja.loja_api.enums;

public enum PaymentStatus {
    PENDING,
    APPROVED,
    DECLINED,
    CANCELED;

    public static PaymentStatus fromMercadoPagoStatus(String status) {
        return switch (status.toLowerCase()) {
            case "approved" -> APPROVED;
            case "pending", "in_process" -> PENDING;
            case "rejected" -> DECLINED;
            case "cancelled" -> CANCELED;
            default -> CANCELED;
        };
    }
}
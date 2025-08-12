package com.loja.loja_api.model;

import com.loja.loja_api.enums.PaymentMethod;
import com.loja.loja_api.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "payments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @Enumerated(EnumType.STRING)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private String provider; // Ex: MERCADO_PAGO
    private String providerPaymentId; // ID retornado pelo Mercado Pago
    private Integer installments;

    @Lob
    private String qrCode;

    @Lob
    private String qrCodeBase64;

    private Instant createdAt;
    private Instant updatedAt;

    private Instant confirmedAt; // ✅ Data de confirmação do pagamento

    @PrePersist
    void prePersist() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
        if (status == null) status = PaymentStatus.PENDING;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }
}

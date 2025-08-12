package com.loja.loja_api.repositories;

import com.loja.loja_api.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // ✅ Busca pagamento pelo ID do Mercado Pago
    Optional<Payment> findByProviderPaymentId(String providerPaymentId);
}

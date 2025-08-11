package com.loja.loja_api.service;

import com.loja.loja_api.dto.CheckoutRequest;

import com.loja.loja_api.model.Customer;
import com.loja.loja_api.model.Order;
import com.loja.loja_api.model.OrderItem;
import com.loja.loja_api.model.Payment;
import com.loja.loja_api.enums.OrderStatus;
import com.loja.loja_api.enums.PaymentStatus;

import com.loja.loja_api.model.PaymentResponse;
import com.loja.loja_api.repositories.OrderRepository;
import com.loja.loja_api.repositories.PaymentRepository;
import com.loja.loja_api.util.QrGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckoutService {

    private final OrderRepository orderRepo;
    private final PaymentRepository paymentRepo;

    public PaymentResponse checkout(CheckoutRequest req) {
        Order order = mapToOrder(req);
        order = orderRepo.save(order);

        Payment payment = Payment.builder()
                .order(order)
                .method(req.getMethod())
                .installments(req.getInstallments())
                .provider("MOCK")
                .status(PaymentStatus.PENDING)
                .build();

        switch (req.getMethod()) {
            case CREDIT, DEBIT -> simulateCardAuth(req, payment);
            case PIX -> buildPix(payment, order);
            case BOLETO -> simulateBoleto(payment, order);
        }

        payment = paymentRepo.save(payment);

        return PaymentResponse.builder()
                .orderId(order.getId())
                .paymentId(payment.getId())
                .status(payment.getStatus())
                .qrCode(payment.getQrCode())
                .qrCodeBase64(payment.getQrCodeBase64())
                .message(statusMessage(payment))
                .build();
    }

    private void simulateCardAuth(CheckoutRequest req, Payment payment) {
        boolean approved = req.getCardToken() != null || "4242".equals(req.getCardLast4());
        payment.setStatus(approved ? PaymentStatus.APPROVED : PaymentStatus.DECLINED);
        payment.getOrder().setStatus(approved ? OrderStatus.PAID : OrderStatus.CREATED);
    }

    private void buildPix(Payment payment, Order order) {
        String pixPayload = "00020126580014BR.GOV.BCB.PIX0136pix@seudominio.com520400005303986540" +
                order.getTotal().toPlainString().replace(".", "") +
                "5802BR5920Sua Loja LTDA6009Sao Paulo62070503***6304ABCD";
        payment.setQrCode(pixPayload);
        payment.setQrCodeBase64(QrGenerator.pngBase64(pixPayload, 360, 360));
        payment.setStatus(PaymentStatus.PENDING);
    }

    private void simulateBoleto(Payment payment, Order order) {
        payment.setStatus(PaymentStatus.PENDING);
    }

    private Order mapToOrder(CheckoutRequest req) {
        Order order = Order.builder()
                .subtotal(req.getSubtotal())
                .shipping(req.getShipping())
                .discount(req.getDiscount())
                .total(req.getTotal())
                .status(OrderStatus.CREATED)
                .customer(Customer.builder()
                        .fullName(req.getFullName())
                        .email(req.getEmail())
                        .phone(req.getPhone())
                        .cpf(req.getCpf())
                        .build())
                .build();

        List<OrderItem> items = req.getItems().stream().map(i ->
                OrderItem.builder()
                        .order(order)
                        .name(i.getName())
                        .quantity(i.getQuantity())
                        .price(i.getPrice())
                        .build()
        ).toList();

        order.setItems(items);
        return order;
    }

    private String statusMessage(Payment payment) {
        return switch (payment.getStatus()) {
            case APPROVED -> "Pagamento aprovado";
            case DECLINED -> "Pagamento recusado";
            case PENDING -> "Aguardando pagamento";
            case CANCELED -> "Pagamento cancelado";
        };
    }
}

package com.loja.loja_api.dto;

import com.loja.loja_api.enums.PaymentMethod;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CheckoutRequest {

    // Dados pessoais
    @NotBlank
    private String fullName;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String phone;

    @NotBlank
    private String cpf;

    // Endereço
    @NotBlank
    private String cep;

    @NotBlank
    private String street;

    @NotBlank
    private String number;

    private String complement;

    @NotBlank
    private String neighborhood;

    @NotBlank
    private String city;

    @NotBlank
    private String state;

    // Pedido
    @NotNull
    private BigDecimal subtotal;

    @NotNull
    private BigDecimal shipping;

    @NotNull
    private BigDecimal discount;

    @NotNull
    private BigDecimal total;

    @NotEmpty
    private List<OrderItemDTO> items;

    // Pagamento
    @NotNull
    private PaymentMethod method; // CREDIT, DEBIT, PIX, BOLETO

    private Integer installments;

    // Token do cartão (gerado no front pelo MP)
    private String cardToken;

    // Bandeira do cartão (visa, master, elo, amex, etc.)
    private String paymentMethodId;
}

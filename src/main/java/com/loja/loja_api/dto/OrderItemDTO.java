package com.loja.loja_api.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemDTO {

    @NotBlank
    private String name;

    @NotNull
    private Integer quantity;

    @NotNull
    private BigDecimal price;
}

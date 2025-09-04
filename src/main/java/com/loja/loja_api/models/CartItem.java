package com.loja.loja_api.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;

    @Column(length = 999) // Aumenta limite do nome
    private String name;

    @Column(columnDefinition = "TEXT", length = 999) // Campo de texto longo
    private String description;

    private Double price;
    private Integer quantity;

    @Column(columnDefinition = "TEXT") // Para URLs longas de imagem
    private String icon;
}
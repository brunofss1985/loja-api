package com.loja.loja_api.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Integer quantity;
    private BigDecimal price;
    private String imageUrl;
    private String variation;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonBackReference("order-items")
    private Order order;
}

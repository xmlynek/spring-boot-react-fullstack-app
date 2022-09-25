package com.filip.managementapp.model;

import lombok.*;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "order_products")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@EqualsAndHashCode
public class OrderProduct implements Serializable {

    @Serial
    private static final long serialVersionUID = 1112345678117399L;

    @EmbeddedId
    private OrderProductKey id;

    @ManyToOne
    @MapsId("orderId")
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "product_order_quantity", nullable = false)
    private Long quantity;

    @Column(name = "product_order_price", nullable = false)
    private Double price;
}

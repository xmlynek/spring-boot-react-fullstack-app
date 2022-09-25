package com.filip.managementapp.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serial;
import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@EqualsAndHashCode
@ToString
public class ShoppingCartItemKey implements Serializable {

    @Serial
    private static final long serialVersionUID = 1732446731961795741L;

    @Column(name = "cart_id", nullable = false)
    private Long cartId;

    @Column(name = "product_id", nullable = false)
    private Long productId;
}

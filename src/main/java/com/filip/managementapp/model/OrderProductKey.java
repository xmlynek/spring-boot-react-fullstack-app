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
public class OrderProductKey implements Serializable {

    @Serial
    private static final long serialVersionUID = 1123446731961795741L;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "product_id", nullable = false)
    private Long productId;
}

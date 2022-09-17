package com.filip.managementapp.model;

import lombok.*;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "products")
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Product implements Serializable {

    @Serial
    private static final long serialVersionUID = 182272432111312L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 128)
    private String name;

    @Column(nullable = false, length = 40)
    private String shortDescription;

    @Column(nullable = false, length = 1024)
    private String description;

    @Column(nullable = false)
    private Long quantity;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Boolean isAvailable;
}

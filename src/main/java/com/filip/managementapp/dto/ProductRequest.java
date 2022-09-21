package com.filip.managementapp.dto;

import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record ProductRequest(
        Long id,

        @NotBlank(message = "Product name is required")
        @Length(max = 128, message = "Product name can be only 128 characters long")
        String name,

        @NotBlank(message = "Short description of the product is required")
        @Length(max = 40, message = "Short description can be only 40 characters long")
        String shortDescription,

        @NotBlank(message = "Product description is required")
        @Length(max = 1024, message = "Description can be only 1024 characters long")
        String description,

        @NotNull(message = "Quantity is required")
        @Min(value = 0, message = "Minimum quantity of product is 0!")
        Long quantity,

        @NotNull(message = "Price is required")
        @Min(value = 0, message = "Minimum price of product is 0.0!")
        Double price,

        @NotNull(message = "Product availability is required")
        Boolean isAvailable,

        MultipartFile productImage
) {
}

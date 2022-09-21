package com.filip.managementapp.dto;

public record ProductDto(
        Long id,
        String name,
        String shortDescription,
        String description,
        Long quantity,
        Double price,
        Boolean isAvailable,
        ImageFileDto productImage
) {
}

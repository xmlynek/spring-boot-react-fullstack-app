package com.filip.managementapp.mapper;

import com.filip.managementapp.dto.ProductDto;
import com.filip.managementapp.dto.ProductRequest;
import com.filip.managementapp.model.Product;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR, uses = { ImageFileMapper.class })
public interface ProductMapper {

    ProductDto productToProductDto(Product product);

    Product productDtoToProduct(ProductDto productDto);

    Product productRequestToProduct(ProductRequest productRequest);

    @Mapping(target = "productImage", ignore = true)
    ProductRequest productToProductRequest(Product product);
}

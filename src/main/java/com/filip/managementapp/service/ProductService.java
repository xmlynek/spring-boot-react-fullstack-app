package com.filip.managementapp.service;

import com.filip.managementapp.dto.ProductDto;
import com.filip.managementapp.dto.ProductRequest;
import com.filip.managementapp.exception.ResourceAlreadyExistsException;
import com.filip.managementapp.exception.ResourceNotFoundException;
import com.filip.managementapp.mapper.ProductMapper;
import com.filip.managementapp.model.Product;
import com.filip.managementapp.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProductService {

    public static final String PRODUCT_BY_ID_NOT_FOUND_STRING = "Product with id %d not found";
    public static final String PRODUCT_WITH_GIVEN_NAME_EXISTS_STRING = "Product with name '%s' already exists";

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Transactional(readOnly = true)
    public List<ProductDto> findAllProducts() {
        return productRepository
                .findAll(Sort.by(Sort.Direction.DESC, "isAvailable"))
                .stream()
                .map(productMapper::productToProductDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductDto findProductById(Long productId) {
        Product product = productRepository
                .findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(PRODUCT_BY_ID_NOT_FOUND_STRING, productId)));
        return productMapper.productToProductDto(product);
    }

    @Transactional
    public ProductDto saveProduct(ProductRequest productRequest) {
        if(productRepository.existsByName(productRequest.name())) {
            throw new ResourceAlreadyExistsException(
                    String.format(PRODUCT_WITH_GIVEN_NAME_EXISTS_STRING, productRequest.name())
            );
        }
        Product productToSave = productMapper.productRequestToProduct(productRequest);

        return productMapper.productToProductDto(productRepository.save(productToSave));
    }

    @Transactional
    public ProductDto updateProduct(Long productId, ProductRequest productRequest) {
        Product currentProduct = productRepository
                .findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(PRODUCT_BY_ID_NOT_FOUND_STRING, productId)));

        if(!Objects.equals(currentProduct.getName(), productRequest.name()) &&
                productRepository.existsByName(productRequest.name())) {
            throw new ResourceAlreadyExistsException(
                    String.format(PRODUCT_WITH_GIVEN_NAME_EXISTS_STRING, productRequest.name())
            );
        }

        Product productToUpdate = productMapper.productRequestToProduct(productRequest);
        productToUpdate.setId(currentProduct.getId());
        if (productToUpdate.getProductImage() == null) {
           productToUpdate.setProductImage(currentProduct.getProductImage());
        }

        return productMapper.productToProductDto(productRepository.save(productToUpdate));
    }

    @Transactional
    public void deleteProduct(Long productId) {
        if(productRepository.existsById(productId)) {
            productRepository.deleteById(productId);
        } else {
            throw new ResourceNotFoundException(String.format(PRODUCT_BY_ID_NOT_FOUND_STRING, productId));
        }
    }
}

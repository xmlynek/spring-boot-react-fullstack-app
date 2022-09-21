package com.filip.managementapp.service;

import com.filip.managementapp.dto.ProductDto;
import com.filip.managementapp.dto.ProductRequest;
import com.filip.managementapp.exception.ResourceAlreadyExistsException;
import com.filip.managementapp.exception.ResourceNotFoundException;
import com.filip.managementapp.mapper.ImageFileMapperImpl;
import com.filip.managementapp.mapper.ProductMapper;
import com.filip.managementapp.mapper.ProductMapperImpl;
import com.filip.managementapp.model.ImageFile;
import com.filip.managementapp.model.Product;
import com.filip.managementapp.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Spy
    private ProductMapper productMapper = new ProductMapperImpl(new ImageFileMapperImpl());

    @InjectMocks
    private ProductService productService;

    private final Product product;

    private final ProductRequest productRequest;

    public ProductServiceTest() {
        this.product = new Product(
                1L,
                "LCD Monitor 123",
                "Modern LCD technology",
                "Great monitor using LCD technology",
                15L,
                199.50,
                true,
                new ImageFile(
                        null,
                        "filename.jpg",
                        MediaType.IMAGE_JPEG_VALUE,
                        "DATA".getBytes())
        );
        this.productRequest = new ProductRequest(
                null,
                "Name",
                "ShortDesc",
                "Descr",
                1235L,
                15.32,
                true,
                null
        );
    }


    @Test
    void shouldFindAllProducts() {
        // given
        List<Product> products = new ArrayList<>(
                List.of(
                    this.product,
                    new Product(
                            2L,
                            "Product name 2",
                            "Short description",
                            "Description fsdkfiasd",
                            1234L,
                            531.50,
                            true
                    ),
                    new Product(
                            3L,
                            "Product name 3",
                            "Short description 3",
                            "Description fsdkfiasd 3",
                            5425231123L,
                            11.54,
                            false
                    )
                )
        );
        Sort isAvailableSort = Sort.by(Sort.Direction.DESC, "isAvailable");
        given(productRepository.findAll(isAvailableSort)).willReturn(products);

        // when
        List<ProductDto> response = productService.findAllProducts();

        // then
        verify(productMapper, times(products.size())).productToProductDto(any());
        assertThat(response)
                .isNotNull()
                .isNotEmpty()
                .hasSize(3)
                .usingRecursiveComparison()
                .isEqualTo(products.stream().map(productMapper::productToProductDto).toList());
        verify(productRepository, times(1)).findAll(isAvailableSort);
    }

    @Test
    void findAllProductsShouldReturnEmptyArray() {
        // given
        Sort isAvailableSort = Sort.by(Sort.Direction.DESC, "isAvailable");
        given(productRepository.findAll(isAvailableSort)).willReturn(new ArrayList<>());

        // when
        List<ProductDto> response = productService.findAllProducts();

        // then
        assertThat(response).isEmpty();
        verify(productRepository, times(1)).findAll(isAvailableSort);
    }

    @Test
    void shouldFindProductById() {
        // given
        Long productId = product.getId();
        given(productRepository.findById(productId)).willReturn(Optional.of(product));

        // when

        ProductDto productDto = productService.findProductById(productId);

        // then
        verify(productMapper, times(1)).productToProductDto(product);
        assertArrayEquals(productDto.productImage().data(), product.getProductImage().getData());
        assertThat(productDto)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(productMapper.productToProductDto(product));
        verify(productRepository, times(1)).findById(productId);

    }

    @Test
    void findProductByIdShouldThrowNotResourceFoundException() {
        // given
        Long productId = product.getId();
        given(productRepository.findById(productId)).willReturn(Optional.empty());

        // when

        assertThatThrownBy(() -> productService.findProductById(productId))
                .isInstanceOf(ResourceNotFoundException.class)
                        .hasMessage(String.format(ProductService.PRODUCT_BY_ID_NOT_FOUND_STRING, productId));

        // then
        verify(productRepository, times(1)).findById(productId);
        verify(productMapper, never()).productToProductDto(any());
    }

    @Test
    void shouldSaveProduct() {
        // given
        Product mappedEntity = productMapper.productRequestToProduct(productRequest);

        Product expectedEntity = productMapper.productRequestToProduct(productRequest);
        expectedEntity.setId(1L);

        given(productRepository.existsByName(productRequest.name())).willReturn(false);
        given(productRepository.save(mappedEntity)).willReturn(expectedEntity);

        // when
        ProductDto savedProductDto = productService.saveProduct(productRequest);

        // then
        assertThat(savedProductDto)
                .isNotNull()
                .isEqualTo(productMapper.productToProductDto(expectedEntity));
        verify(productRepository, times(1)).existsByName(productRequest.name());
        verify(productRepository, times(1)).save(mappedEntity);
    }

    @Test
    void saveProductShouldThrowResourceAlreadyExistsException() {
        // given
        String productName = productRequest.name();
        given(productRepository.existsByName(productName)).willReturn(true);

        // when
        // then
        assertThatThrownBy(() -> productService.saveProduct(productRequest))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                        .hasMessage(String.format(ProductService.PRODUCT_WITH_GIVEN_NAME_EXISTS_STRING, productName));
        verify(productRepository, times(1)).existsByName(productName);
        verify(productRepository, never()).save(any());
    }

    @Test
    void shouldUpdateProduct() {
        // given
        Long productId = this.product.getId();

        Product productToUpdate = productMapper.productRequestToProduct(productRequest);
        productToUpdate.setId(productId);

        given(productRepository.findById(productId)).willReturn(Optional.of(this.product));
        given(productRepository.existsByName(productRequest.name())).willReturn(false);
        given(productRepository.save(any())).willReturn(productToUpdate);

        // when
        ProductDto updatedProductDto = productService.updateProduct(productId, productRequest);

        // then
        assertThat(updatedProductDto)
                .isNotNull()
                .isEqualTo(productMapper.productToProductDto(productToUpdate));
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).existsByName(productRequest.name());
        verify(productRepository, times(1)).save(any());
    }

    @Test
    void updateProductShouldThrowResourceAlreadyExistsException() {
        // given
        Long productId = 1L;
        given(productRepository.findById(productId)).willReturn(Optional.of(this.product));
        given(productRepository.existsByName(productRequest.name())).willReturn(true);

        // when
        // then
        assertThatThrownBy(() -> productService.updateProduct(productId, productRequest))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessage(String.format(ProductService.PRODUCT_WITH_GIVEN_NAME_EXISTS_STRING, productRequest.name()));
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).existsByName(productRequest.name());
        verify(productRepository, never()).save(any());
    }

    @Test
    void updateProductShouldThrowResourceNotFoundException() {
        // given
        Long productId = 1L;
        given(productRepository.findById(productId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> productService.updateProduct(productId, productRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(String.format(ProductService.PRODUCT_BY_ID_NOT_FOUND_STRING, productId));
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, never()).existsByName(any());
        verify(productRepository, never()).save(any());
    }

    @Test
    void shouldDeleteProduct() {
        // given
        Long productId = this.product.getId();
        given(productRepository.existsById(productId)).willReturn(true);

        // when
        productService.deleteProduct(productId);

        // then
        verify(productRepository, times(1)).existsById(productId);
        verify(productRepository, times(1)).deleteById(productId);
    }

    @Test
    void deleteProductShouldThrowResourceNotFoundException() {
        // given
        Long productId = this.product.getId();
        given(productRepository.existsById(productId)).willReturn(false);

        // when
        // then
        assertThatThrownBy(() -> productService.deleteProduct(productId))
                .isInstanceOf(ResourceNotFoundException.class)
                        .hasMessage(String.format(ProductService.PRODUCT_BY_ID_NOT_FOUND_STRING, productId));
        verify(productRepository, times(1)).existsById(productId);
        verify(productRepository, never()).deleteById(productId);
    }
}
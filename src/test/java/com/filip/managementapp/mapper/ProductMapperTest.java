package com.filip.managementapp.mapper;

import com.filip.managementapp.dto.ImageFileDto;
import com.filip.managementapp.dto.ProductDto;
import com.filip.managementapp.dto.ProductRequest;
import com.filip.managementapp.model.ImageFile;
import com.filip.managementapp.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Assertions.assertThat;

class ProductMapperTest {

    private final ProductMapper productMapper;

    private final Product product;

    private Product variableProduct;

    private final ProductDto productDto;

    public ProductMapperTest() {
        this.productMapper = new ProductMapperImpl(new ImageFileMapperImpl());
        this.product = new Product(
            1L,
            "LCD Monitor 123",
            "Modern LCD technology",
            "Great monitor using LCD technology",
            15L,
            199.50,
            true,
            new ImageFile(
                    1L,
                    "filename.jpg",
                    MediaType.IMAGE_JPEG_VALUE,
                    "DATA".getBytes()
            )
        );
        this.productDto = new ProductDto(
                product.getId(),
                product.getName(),
                product.getShortDescription(),
                product.getDescription(),
                product.getQuantity(),
                product.getPrice(),
                product.getIsAvailable(),
                new ImageFileDto(
                        product.getProductImage().getFilename(),
                        product.getProductImage().getContentType(),
                        product.getProductImage().getData()
                )
        );
    }

    @BeforeEach
    void setUp() {
        this.variableProduct = new Product(
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
    }

    @Test
    void shouldMapProductToProductDtoWithProductImageDto() {
        ProductDto output = productMapper.productToProductDto(product);

        assertThat(output)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(productDto);
    }

    @Test
    void shouldMapProductToProductDtoWithoutProductImageDto() {
        variableProduct.setProductImage(null);
        ProductDto expectedOutput = new ProductDto(
                variableProduct.getId(),
                variableProduct.getName(),
                variableProduct.getShortDescription(),
                variableProduct.getDescription(),
                variableProduct.getQuantity(),
                variableProduct.getPrice(),
                variableProduct.getIsAvailable(),
                null
        );
        ProductDto output = productMapper.productToProductDto(variableProduct);

        assertThat(output)
                .isNotNull()
                .hasNoNullFieldsOrPropertiesExcept("productImage")
                .isEqualTo(expectedOutput);
    }

    @Test
    void productToProductDtoShouldReturnNull() {
        assertThat(productMapper.productToProductDto(null)).isNull();
    }

    @Test
    void shouldMapProductDtoToProduct() {
        Product output = productMapper.productDtoToProduct(productDto);

        assertThat(output)
                .isNotNull()
                .hasNoNullFieldsOrProperties()
                .isEqualTo(variableProduct);
    }

    @Test
    void shouldMapProductDtoToProductWithoutProductImage() {
        ProductDto input = new ProductDto(
                variableProduct.getId(),
                variableProduct.getName(),
                variableProduct.getShortDescription(),
                variableProduct.getDescription(),
                variableProduct.getQuantity(),
                variableProduct.getPrice(),
                variableProduct.getIsAvailable(),
                null
        );
        variableProduct.setProductImage(null);

        Product output = productMapper.productDtoToProduct(input);

        assertThat(output)
                .isNotNull()
                .isEqualTo(variableProduct);
    }

    @Test
    void productDtoToProductShouldReturnNull() {
        assertThat(productMapper.productDtoToProduct(null)).isNull();
    }

    @Test
    void shouldMapProductRequestToProductWithProductImage() {
        variableProduct.setId(null);
        ProductRequest productRequest = new ProductRequest(
                null,
                product.getName(),
                product.getShortDescription(),
                product.getDescription(),
                product.getQuantity(),
                product.getPrice(),
                product.getIsAvailable(),
                new MockMultipartFile(
                        "name",
                        "filename.jpg",
                        MediaType.IMAGE_JPEG_VALUE,
                        "DATA".getBytes()
                )
        );

        Product output = productMapper.productRequestToProduct(productRequest);

        assertThat(output)
                .isNotNull()
                .isEqualTo(variableProduct);
    }

    @Test
    void shouldMapProductRequestToProductWithoutProductImage() {
        variableProduct.setId(null);
        variableProduct.setProductImage(null);

        ProductRequest productRequest = new ProductRequest(
                null,
                product.getName(),
                product.getShortDescription(),
                product.getDescription(),
                product.getQuantity(),
                product.getPrice(),
                product.getIsAvailable(),
                null
        );

        Product output = productMapper.productRequestToProduct(productRequest);

        assertThat(output)
                .isNotNull()
                .hasNoNullFieldsOrPropertiesExcept("productImage", "id")
                .isEqualTo(variableProduct);
    }

    @Test
    void productRequestToProductShouldReturnNull() {
        assertThat(productMapper.productRequestToProduct(null)).isNull();
    }

    @Test
    void shouldMapProductToProductRequest() {
        ProductRequest expectedOutput = new ProductRequest(
                product.getId(),
                product.getName(),
                product.getShortDescription(),
                product.getDescription(),
                product.getQuantity(),
                product.getPrice(),
                product.getIsAvailable(),
                null
        );

        ProductRequest output = productMapper.productToProductRequest(product);

        assertThat(output)
                .isNotNull()
                .hasNoNullFieldsOrPropertiesExcept("productImage")
                .isEqualTo(expectedOutput);
    }

    @Test
    void productToProductRequestShouldReturnNull() {
        assertThat(productMapper.productToProductRequest(null)).isNull();
    }
}
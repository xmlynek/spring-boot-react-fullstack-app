package com.filip.managementapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.filip.managementapp.AbstractControllerITest;
import com.filip.managementapp.dto.ProductDto;
import com.filip.managementapp.exception.ResourceAlreadyExistsException;
import com.filip.managementapp.exception.ResourceNotFoundException;
import com.filip.managementapp.mapper.ProductMapper;
import com.filip.managementapp.model.Product;
import com.filip.managementapp.repository.ProductRepository;
import com.filip.managementapp.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProductControllerTest extends AbstractControllerITest {

    private final String PRODUCTS_API_URL = "/api/v1/products";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductMapper productMapper = Mappers.getMapper(ProductMapper.class);

    private final Product product;

    public ProductControllerTest() {
        this.product = new Product(
                null,
                "LCD Monitor 123",
                "Modern LCD technology",
                "Great monitor using LCD technology",
                15L,
                199.50,
                true
        );
    }

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
    }

    @Test
    void findAllProductsShouldListOfProducts() throws Exception {
        List<Product> products = new ArrayList<>(
                List.of(
                        this.product,
                        new Product(
                                null,
                                "Product name 2",
                                "Short description",
                                "Description fsdkfiasd",
                                1234L,
                                531.50,
                                false
                        ),
                        new Product(
                                null,
                                "Product name 3",
                                "Short description 3",
                                "Description fsdkfiasd 3",
                                5425231123L,
                                11.54,
                                true
                        )
                )
        );

        List<Product> savedProducts = productRepository.saveAllAndFlush(products);

        MvcResult mvcResult = mockMvc.perform(get(PRODUCTS_API_URL))
                .andExpect(status().isOk())
                .andReturn();

        List<ProductDto> productDtos = List.of(objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ProductDto[].class));

        assertThat(productDtos)
                .isNotNull()
                .isNotEmpty()
                .hasSize(products.size())
                .containsAll(savedProducts.stream().map(productMapper::productToProductDto).toList());

        assertThat(productDtos.get(0)).hasFieldOrPropertyWithValue("isAvailable", true);
        assertThat(productDtos.get(productDtos.size() - 1)).hasFieldOrPropertyWithValue("isAvailable", false);
    }

    @Test
    void findAllProductsShouldReturnEmptyList() throws Exception {
        mockMvc.perform(get(PRODUCTS_API_URL))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void findProductByIdShouldReturnProduct() throws Exception {
        Product savedProduct = productRepository.saveAndFlush(this.product);
        Long productId = savedProduct.getId();

        mockMvc.perform(get(PRODUCTS_API_URL + "/" + productId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(savedProduct)));
    }

    @Test
    void findProductByIdShouldReturnResourceNotFoundApiException() throws Exception {
        Product savedProduct = productRepository.saveAndFlush(this.product);
        long productId = savedProduct.getId() + 1L;

        MvcResult mvcResult = mockMvc.perform(get(PRODUCTS_API_URL + "/" + productId))
                .andExpect(status().isNotFound())
                .andReturn();

        String exceptionMessage = String.format(ProductService.PRODUCT_BY_ID_NOT_FOUND_STRING, productId);
        assertThat(mvcResult.getResolvedException())
                .isNotNull()
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(exceptionMessage);
        assertThat(mvcResult.getResponse().getContentAsString()).contains(exceptionMessage);
    }

    @Test
    @WithMockUser(username = "username", roles = "ADMIN")
    void saveProductShouldSaveAndReturnProduct() throws Exception {
        ProductDto productDtoRequest = productMapper.productToProductDto(this.product);

        MvcResult mvcResult = mockMvc.perform(
                        post(PRODUCTS_API_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(productDtoRequest))
                )
                .andExpect(status().isCreated())
                .andReturn();

        ProductDto savedProductDtoResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ProductDto.class);
        List<Product> savedProducts = productRepository.findAll();

        assertThat(savedProducts)
                .isNotEmpty()
                .hasSize(1);
        assertThat(savedProductDtoResponse)
                .isNotNull()
                .isEqualTo(productMapper.productToProductDto(savedProducts.get(0)))
                .hasNoNullFieldsOrProperties()
                .hasFieldOrPropertyWithValue("name", this.product.getName())
                .hasFieldOrPropertyWithValue("price", this.product.getPrice())
                .hasFieldOrPropertyWithValue("isAvailable", this.product.getIsAvailable())
                .hasFieldOrPropertyWithValue("shortDescription", this.product.getShortDescription())
                .hasFieldOrPropertyWithValue("description", this.product.getDescription())
                .hasFieldOrPropertyWithValue("id", savedProducts.get(0).getId())
                .hasFieldOrPropertyWithValue("quantity", this.product.getQuantity());
    }

    @Test
    @WithMockUser(username = "username", roles = "ADMIN")
    void saveProductShouldReturnResourceAlreadyExistsApiException() throws Exception {
        ProductDto productDtoRequest = productMapper.productToProductDto(this.product);
        productRepository.saveAndFlush(this.product);

        MvcResult mvcResult = mockMvc.perform(
                        post(PRODUCTS_API_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(productDtoRequest))
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        String exceptionMessage = String.format(ProductService.PRODUCT_WITH_GIVEN_NAME_EXISTS_STRING, productDtoRequest.name());

        assertThat(mvcResult.getResolvedException())
                .isNotNull()
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessage(exceptionMessage);
        assertThat(mvcResult.getResponse().getContentAsString()).contains(exceptionMessage);
    }

    @Test
    void saveProductShouldReturnUnauthorized() throws Exception {
        ProductDto productDtoRequest = productMapper.productToProductDto(this.product);

        mockMvc.perform(
                        post(PRODUCTS_API_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(productDtoRequest))
                )
                .andExpect(status().isUnauthorized());

        List<Product> products = productRepository.findAll();

        assertThat(products).isEmpty();
    }

    @Test
    @WithMockUser(username = "username")
    void saveProductShouldReturnForbidden() throws Exception {
        ProductDto productDtoRequest = productMapper.productToProductDto(this.product);

        mockMvc.perform(
                        post(PRODUCTS_API_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(productDtoRequest))
                )
                .andExpect(status().isForbidden());

        List<Product> products = productRepository.findAll();

        assertThat(products).isEmpty();
    }

    @Test
    @WithMockUser(username = "username", roles = "ADMIN")
    void shouldUpdateProduct() throws Exception {
        Product savedProduct = productRepository.saveAndFlush(this.product);
        ProductDto productRequest = new ProductDto(
                null,
                "Updated name",
                "Updated short description",
                "Updated description",
                11L,
                555.55,
                false
        );
        Long productId = savedProduct.getId();

        ProductDto expectedProductDtoResponse = new ProductDto(
                productId,
                productRequest.name(),
                productRequest.shortDescription(),
                productRequest.description(),
                productRequest.quantity(),
                productRequest.price(),
                productRequest.isAvailable()
        );

        mockMvc.perform(
                put(PRODUCTS_API_URL + "/" + productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest))
        )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedProductDtoResponse)));

        Optional<Product> savedUpdatedProduct = productRepository.findById(productId);
        List<Product> savedProducts = productRepository.findAll();

        assertThat(savedUpdatedProduct)
                .isNotNull()
                .isPresent()
                .contains(productMapper.productDtoToProduct(expectedProductDtoResponse));
        assertThat(savedProducts.size()).isOne();
    }

    @Test
    @WithMockUser(username = "username", roles = "ADMIN")
    void updateProductShouldReturnResourceNotFoundApiException() throws Exception {
        Product savedProduct = productRepository.saveAndFlush(this.product);
        ProductDto productRequest = new ProductDto(
                null,
                "Updated name",
                "Updated short description",
                "Updated description",
                11L,
                555.55,
                false
        );
        long productId = savedProduct.getId() + 1;

        MvcResult mvcResult = mockMvc.perform(
                        put(PRODUCTS_API_URL + "/" + productId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(productRequest))
                )
                .andExpect(status().isNotFound())
                .andReturn();

        String exceptionMessage = String.format(ProductService.PRODUCT_BY_ID_NOT_FOUND_STRING, productId);
        Optional<Product> savedUpdatedProduct = productRepository.findById(savedProduct.getId());

        assertThat(mvcResult.getResolvedException())
                .isNotNull()
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(exceptionMessage);
        assertThat(savedUpdatedProduct)
                .isNotNull()
                .isPresent()
                .contains(this.product);
    }

    @Test
    @WithMockUser(username = "username", roles = "ADMIN")
    void updateProductShouldReturnResourceAlreadyExistsApiException() throws Exception {
        Product savedProduct = productRepository.saveAndFlush(this.product);
        Product anotherSavedProduct = productRepository.saveAndFlush(new Product(
                null,
                "Product123",
                "Short description 3",
                "Description fsdkfiasd 3",
                5425231123L,
                11.54,
                true
        ));

        ProductDto productRequest = new ProductDto(
                null,
                anotherSavedProduct.getName(),
                "Updated short description",
                "Updated description",
                11L,
                555.55,
                false
        );
        long productId = savedProduct.getId();

        MvcResult mvcResult = mockMvc.perform(
                        put(PRODUCTS_API_URL + "/" + productId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(productRequest))
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        String exceptionMessage = String.format(ProductService.PRODUCT_WITH_GIVEN_NAME_EXISTS_STRING, productRequest.name());
        Optional<Product> savedUpdatedProduct = productRepository.findById(savedProduct.getId());
        Optional<Product> otherSavedProduct = productRepository.findById(anotherSavedProduct.getId());

        assertThat(mvcResult.getResolvedException())
                .isNotNull()
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessage(exceptionMessage);
        assertThat(savedUpdatedProduct)
                .isNotNull()
                .isPresent()
                .contains(this.product);
        assertThat(otherSavedProduct)
                .isNotNull()
                .isPresent()
                .contains(anotherSavedProduct);
    }

    @Test
    void updateUserShouldReturnUnauthorized() throws Exception {
        Product savedProduct = productRepository.saveAndFlush(this.product);
        ProductDto productRequest = new ProductDto(
                null,
                "Updated name",
                "Updated short description",
                "Updated description",
                11L,
                555.55,
                false
        );
        Long productId = savedProduct.getId();

        mockMvc.perform(
                put(PRODUCTS_API_URL + "/" + productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest))
                )
                .andExpect(status().isUnauthorized());

        Optional<Product> existingProduct = productRepository.findById(savedProduct.getId());

        assertThat(existingProduct).isNotNull().isPresent().contains(this.product);
    }

    @Test
    @WithMockUser(username = "username")
    void updateUserShouldReturnForbidden() throws Exception {
        Product savedProduct = productRepository.saveAndFlush(this.product);
        ProductDto productRequest = new ProductDto(
                null,
                "Updated name",
                "Updated short description",
                "Updated description",
                11L,
                555.55,
                false
        );
        Long productId = savedProduct.getId();

        mockMvc.perform(
                        put(PRODUCTS_API_URL + "/" + productId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(productRequest))
                )
                .andExpect(status().isForbidden());

        Optional<Product> existingProduct = productRepository.findById(savedProduct.getId());

        assertThat(existingProduct).isNotNull().isPresent().contains(this.product);
    }

    @Test
    @WithMockUser(username = "username", roles = "ADMIN")
    void deleteProductByIdShouldDeleteAndReturnNoContent() throws Exception {
        Product savedProduct = productRepository.save(this.product);
        Long productId = savedProduct.getId();

        mockMvc.perform(delete(PRODUCTS_API_URL + "/" + productId))
                .andExpect(status().isNoContent());

        Optional<Product> foundedProduct = productRepository.findById(productId);
        List<Product> products = productRepository.findAll();

        assertThat(foundedProduct).isNotPresent();
        assertThat(products).isEmpty();
    }

    @Test
    @WithMockUser(username = "username", roles = "ADMIN")
    void deleteProductByIdShouldReturnResourceNotFoundExceptionApiException() throws Exception {
        Product savedProduct = productRepository.save(this.product);
        long productId = savedProduct.getId() + 1;

        MvcResult mvcResult = mockMvc.perform(delete(PRODUCTS_API_URL + "/" + productId))
                .andExpect(status().isNotFound())
                .andReturn();

        Optional<Product> foundedProduct = productRepository.findById(savedProduct.getId());
        String exceptionMessage = String.format(ProductService.PRODUCT_BY_ID_NOT_FOUND_STRING, productId);

        assertThat(foundedProduct).isPresent();
        assertThat(mvcResult.getResolvedException())
                .isNotNull()
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(exceptionMessage);
        assertThat(mvcResult.getResponse().getContentAsString()).contains(exceptionMessage);
    }

    @Test
    void deleteProductByIdShouldReturnUnauthorized() throws Exception {
        Product savedProduct = productRepository.save(this.product);
        Long productId = savedProduct.getId();

        mockMvc.perform(delete(PRODUCTS_API_URL + "/" + productId))
                .andExpect(status().isUnauthorized());

        Optional<Product> foundedProduct = productRepository.findById(savedProduct.getId());
        assertThat(foundedProduct).isPresent();
    }

    @Test
    @WithMockUser(username = "username")
    void deleteProductByIdShouldReturnForbidden() throws Exception {
        Product savedProduct = productRepository.save(this.product);
        Long productId = savedProduct.getId();

        mockMvc.perform(delete(PRODUCTS_API_URL + "/" + productId))
                .andExpect(status().isForbidden());

        Optional<Product> foundedProduct = productRepository.findById(savedProduct.getId());
        assertThat(foundedProduct).isPresent();
    }
}
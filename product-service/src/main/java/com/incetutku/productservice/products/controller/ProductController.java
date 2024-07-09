package com.incetutku.productservice.products.controller;

import com.incetutku.productservice.products.dto.ProductDto;
import com.incetutku.productservice.products.model.Product;
import com.incetutku.productservice.products.repository.ProductRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletionException;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private static final Logger LOGGER = LogManager.getLogger(ProductController.class);
    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        LOGGER.info("Get all products");
        List<ProductDto> productDtos = new ArrayList<>();
        productRepository.findAll().items().subscribe(product -> productDtos.add(new ProductDto(product))).join();

        return ResponseEntity.ok(productDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable String id) {
        LOGGER.info("Get product By Id: {}", id);
        Product product = productRepository.findById(id).join();
        if (Objects.isNull(product)) return new ResponseEntity<>("Product not found", HttpStatus.NOT_FOUND);
        return ResponseEntity.ok(new ProductDto(product));
    }

    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto productDto) {
        Product product = ProductDto.toProduct(productDto);
        product.setId(UUID.randomUUID().toString());
        productRepository.save(product).join();
        LOGGER.info("Product created with Id: {}", product.getId());
        return new ResponseEntity<>(new ProductDto(product), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProductById(@PathVariable String id) {
        LOGGER.info("Deleted product By Id: {}", id);
        Product product = productRepository.deleteById(id).join();
        if (Objects.isNull(product)) return new ResponseEntity<>("Product not found", HttpStatus.NOT_FOUND);
        return ResponseEntity.ok(new ProductDto(product));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@RequestBody ProductDto productDto, @PathVariable String id) {
        try {
            Product updatedProduct = productRepository.updateById(ProductDto.toProduct(productDto), id).join();
            LOGGER.info("Product updated with Id: {}", updatedProduct.getId());
            return new ResponseEntity<>(new ProductDto(updatedProduct), HttpStatus.OK);
        } catch (CompletionException exception) {
            return new ResponseEntity<>("Product not found", HttpStatus.NOT_FOUND);
        }

    }
}

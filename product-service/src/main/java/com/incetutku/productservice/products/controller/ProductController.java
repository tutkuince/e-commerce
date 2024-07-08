package com.incetutku.productservice.products.controller;

import com.incetutku.productservice.products.dto.ProductDto;
import com.incetutku.productservice.products.model.Product;
import com.incetutku.productservice.products.repository.ProductRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
}

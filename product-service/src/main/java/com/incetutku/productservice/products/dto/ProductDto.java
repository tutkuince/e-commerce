package com.incetutku.productservice.products.dto;

import com.incetutku.productservice.products.model.Product;

public record ProductDto(
        String id,
        String name,
        String code,
        float price,
        String model
) {
    public ProductDto(Product product) {
        this(
                product.getId(),
                product.getProductName(),
                product.getCode(),
                product.getPrice(),
                product.getModel()
        );
    }

    public static Product toProduct(ProductDto productDto) {
        Product product = new Product();
        product.setId(product.getId());
        product.setProductName(product.getProductName());
        product.setCode(product.getCode());
        product.setPrice(product.getPrice());
        product.setModel(product.getModel());
        return product;
    }
}

package com.incetutku.productservice.products.exception;

import com.incetutku.productservice.products.enums.ProductError;
import org.springframework.lang.Nullable;

public class ProductException extends Exception {
    private final ProductError productError;
    @Nullable
    private final String productId;

    public ProductException(ProductError productError, @Nullable String productId) {
        this.productError = productError;
        this.productId = productId;
    }

    public ProductError getProductError() {
        return productError;
    }

    @Nullable
    public String getProductId() {
        return productId;
    }
}

package com.incetutku.productservice.products.enums;

import org.springframework.http.HttpStatus;
import software.amazon.awssdk.services.dynamodb.endpoints.internal.Value;

public enum ProductError {
    PRODUCT_NOT_FOUND("Product not found", HttpStatus.NOT_FOUND);

    private final String message;
    private final HttpStatus httpStatus;

    ProductError(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}

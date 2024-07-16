package com.incetutku.productservice.products.exception;

import com.incetutku.productservice.products.dto.ProductErrorResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ProductExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger LOG = LogManager.getLogger(ProductExceptionHandler.class);

    @ExceptionHandler(value = {ProductException.class})
    protected ResponseEntity<Object> handleProductError(ProductException productException, WebRequest webRequest) {
        ProductErrorResponse response = new ProductErrorResponse(
                productException.getProductError().getMessage(),
                productException.getProductError().getHttpStatus().value(),
                ThreadContext.get("requestId"),
                productException.getProductId()
        );
        LOG.error(productException.getProductError().getMessage());

        return handleExceptionInternal(
                productException,
                response,
                new HttpHeaders(),
                productException.getProductError().getHttpStatus(),
                webRequest
        );
    }
}

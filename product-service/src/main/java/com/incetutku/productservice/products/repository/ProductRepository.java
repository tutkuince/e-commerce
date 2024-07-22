package com.incetutku.productservice.products.repository;

import com.amazonaws.xray.spring.aop.XRayEnabled;
import com.incetutku.productservice.products.enums.ProductError;
import com.incetutku.productservice.products.exception.ProductException;
import com.incetutku.productservice.products.model.Product;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.PagePublisher;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Repository
@XRayEnabled
public class ProductRepository {
    private static final Logger LOG = LogManager.getLogger(ProductRepository.class);

    private final DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient;
    private final DynamoDbAsyncTable<Product> productTable;

    public ProductRepository(DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient, @Value("${aws.product.db.name}") String productDBName) {
        this.dynamoDbEnhancedAsyncClient = dynamoDbEnhancedAsyncClient;
        this.productTable = dynamoDbEnhancedAsyncClient.table(productDBName, TableSchema.fromBean(Product.class));
    }

    public PagePublisher<Product> findAll() {
        // DO NOT DO THIS PRODUCTION
        return productTable.scan();
    }

    public CompletableFuture<Product> findById(String productId) {
        LOG.info("ProductId: {}", productId);
        return productTable.getItem(Key.builder()
                .partitionValue(productId)
                .build());
    }

    public CompletableFuture<Void> save(Product product) throws ProductException {
        Product productWithSameCode = checkIfCodeExists(product.getCode()).join();
        if (!Objects.isNull(productWithSameCode))
            throw new ProductException(ProductError.PRODUCT_CODE_ALREADY_EXISTS, productWithSameCode.getId());
        return productTable.putItem(product);
    }

    public CompletableFuture<Product> deleteById(String productId) {
        return productTable.deleteItem(Key.builder()
                .partitionValue(productId)
                .build());
    }

    public CompletableFuture<Product> updateById(Product product, String productId) throws ProductException {
        product.setId(productId);
        Product productWithSameCode = checkIfCodeExists(product.getCode()).join();
        if (!Objects.isNull(productWithSameCode) && !productWithSameCode.getId().equals(product.getId()))
            throw new ProductException(ProductError.PRODUCT_CODE_ALREADY_EXISTS, productWithSameCode.getId());

        return productTable.updateItem(UpdateItemEnhancedRequest.<Product>builder(
                        Product.class
                )
                .item(product)
                .conditionExpression(Expression.builder()
                        .expression("attribute_exists(id)")
                        .build())
                .build());
    }

    public CompletableFuture<Product> getByCode(String code) {
        Product productByCode = checkIfCodeExists(code).join();
        if (productByCode != null) {
            return findById(productByCode.getId());
        }
        return CompletableFuture.supplyAsync(() -> null);
    }

    private CompletableFuture<Product> checkIfCodeExists(String code) {
        List<Product> products = new ArrayList<>();
        productTable.index("codeIdx").query(QueryEnhancedRequest.builder()
                .limit(1)
                .queryConditional(QueryConditional.keyEqualTo(Key.builder()
                        .partitionValue(code)
                        .build()))
                .build()).subscribe(productPage -> {
            products.addAll(productPage.items());
        }).join();
        if (!products.isEmpty()) {
            return CompletableFuture.supplyAsync(() -> products.get(0));
        }
        return CompletableFuture.supplyAsync(() -> null);
    }
}

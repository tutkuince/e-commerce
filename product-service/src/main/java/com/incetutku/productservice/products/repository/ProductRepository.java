package com.incetutku.productservice.products.repository;

import com.amazonaws.xray.spring.aop.XRayEnabled;
import com.incetutku.productservice.products.model.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.PagePublisher;
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest;

import java.util.concurrent.CompletableFuture;

@Repository
@XRayEnabled
public class ProductRepository {
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
        return productTable.getItem(Key.builder()
                .partitionValue(productId)
                .build());
    }

    public CompletableFuture<Void> save(Product product) {
        return productTable.putItem(product);
    }

    public CompletableFuture<Product> deleteById(String productId) {
        return productTable.deleteItem(Key.builder()
                .partitionValue(productId)
                .build());
    }

    public CompletableFuture<Product> updateById(Product product, String productId) {
        product.setId(productId);
        return productTable.updateItem(UpdateItemEnhancedRequest.<Product>builder(
                        Product.class
                )
                .item(product)
                .conditionExpression(Expression.builder()
                        .expression("attribute_exists(id)")
                        .build())
                .build());
    }
}

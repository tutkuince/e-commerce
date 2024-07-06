package com.incetutku.productservice.products.repository;

import com.incetutku.productservice.products.model.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PagePublisher;

import java.util.concurrent.CompletableFuture;

@Repository
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
}

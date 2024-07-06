package com.incetutku.productservice.products.model;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
public record Product(
        @DynamoDbPartitionKey
        String id,
        String productName,
        String code,
        float price,
        String model
) {
}

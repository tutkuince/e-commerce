package com.myorg;

import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ecr.Repository;
import software.amazon.awscdk.services.ecr.RepositoryProps;
import software.amazon.awscdk.services.ecr.TagMutability;
import software.constructs.Construct;

public class EcrStack extends Stack {
    private final Repository productServiceRepository;

    public EcrStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        this.productServiceRepository = new Repository(this, "product-service", RepositoryProps.builder()
                .repositoryName("product-service")
                .removalPolicy(RemovalPolicy.DESTROY)
                .imageTagMutability(TagMutability.IMMUTABLE)
                .build());
    }

    public Repository getProductServiceRepository() {
        return productServiceRepository;
    }
}

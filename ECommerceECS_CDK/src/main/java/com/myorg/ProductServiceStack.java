package com.myorg;

import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.Vpc;
import software.amazon.awscdk.services.ecr.Repository;
import software.amazon.awscdk.services.ecs.*;
import software.amazon.awscdk.services.elasticloadbalancingv2.ApplicationLoadBalancer;
import software.amazon.awscdk.services.elasticloadbalancingv2.NetworkLoadBalancer;
import software.amazon.awscdk.services.logs.LogGroup;
import software.amazon.awscdk.services.logs.LogGroupProps;
import software.amazon.awscdk.services.logs.RetentionDays;
import software.constructs.Construct;

public class ProductServiceStack extends Stack {

    public ProductServiceStack(final Construct scope, final String id, StackProps stackProps, ProductServiceProps productServiceProps) {
        super(scope, id, stackProps);

        FargateTaskDefinition fargateTaskDefinition = new FargateTaskDefinition(this, "TaskDefinition", FargateTaskDefinitionProps.builder()
                .family("product-service")
                .cpu(512)
                .memoryLimitMiB(1024)
                .build());

        AwsLogDriver logDriver = new AwsLogDriver(AwsLogDriverProps.builder()
                .logGroup(new LogGroup(this, "LogGroup", LogGroupProps.builder()
                        .logGroupName("ProductService")
                        .removalPolicy(RemovalPolicy.DESTROY)
                        .retention(RetentionDays.ONE_MONTH)
                        .build()))
                .streamPrefix("ProductService")
                .build());
    }
}

record ProductServiceProps(
        Vpc vpc,
        Cluster cluster,
        NetworkLoadBalancer networkLoadBalancer,
        ApplicationLoadBalancer applicationLoadBalancer,
        Repository repository
) {}
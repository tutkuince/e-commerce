package com.myorg;

import software.amazon.awscdk.Duration;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.dynamodb.*;
import software.amazon.awscdk.services.ec2.Peer;
import software.amazon.awscdk.services.ec2.Port;
import software.amazon.awscdk.services.ec2.Vpc;
import software.amazon.awscdk.services.ecr.Repository;
import software.amazon.awscdk.services.ecs.*;
import software.amazon.awscdk.services.ecs.Protocol;
import software.amazon.awscdk.services.elasticloadbalancingv2.*;
import software.amazon.awscdk.services.elasticloadbalancingv2.HealthCheck;
import software.amazon.awscdk.services.logs.LogGroup;
import software.amazon.awscdk.services.logs.LogGroupProps;
import software.amazon.awscdk.services.logs.RetentionDays;
import software.constructs.Construct;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static software.amazon.awscdk.services.elasticloadbalancingv2.Protocol.*;

public class ProductServiceStack extends Stack {

    public ProductServiceStack(final Construct scope, final String id, StackProps stackProps, ProductServiceProps productServiceProps) {
        super(scope, id, stackProps);

        Table productDB = new Table(this, "ProductDB", TableProps.builder()
                .partitionKey(Attribute.builder()
                        .name("id")
                        .type(AttributeType.STRING)
                        .build())
                .tableName("product")
                .removalPolicy(RemovalPolicy.DESTROY)
                .billingMode(BillingMode.PROVISIONED)
                .readCapacity(1)
                .writeCapacity(1)
                .build());


        FargateTaskDefinition fargateTaskDefinition = new FargateTaskDefinition(this, "TaskDefinition", FargateTaskDefinitionProps.builder()
                .family("product-service")
                .cpu(512)
                .memoryLimitMiB(1024)
                .build());
        productDB.grantReadWriteData(fargateTaskDefinition.getTaskRole());

        AwsLogDriver logDriver = new AwsLogDriver(AwsLogDriverProps.builder()
                .logGroup(new LogGroup(this, "LogGroup", LogGroupProps.builder()
                        .logGroupName("ProductService")
                        .removalPolicy(RemovalPolicy.DESTROY)
                        .retention(RetentionDays.ONE_MONTH)
                        .build()))
                .streamPrefix("ProductService")
                .build());

        Map<String, String> envVariables = new HashMap<>();
        envVariables.put("SERVER_PORT", "8080");
        envVariables.put("AWS_PRODUCT_DB_NAME", productDB.getTableName());
        envVariables.put("AWS_REGION", this.getRegion());

        fargateTaskDefinition.addContainer("ProductServiceContainer", ContainerDefinitionOptions.builder()
                .image(ContainerImage.fromEcrRepository(productServiceProps.repository(), "1.1.0"))
                .containerName("productService")
                .logging(logDriver)
                .portMappings(Collections.singletonList(PortMapping.builder()
                        .containerPort(8080)
                        .protocol(Protocol.TCP)
                        .build()))
                .environment(envVariables)
                .build());

        ApplicationListener applicationListener = productServiceProps.applicationLoadBalancer()
                .addListener("ProductServiceALBListener", ApplicationListenerProps.builder()
                        .port(8080)
                        .protocol(ApplicationProtocol.HTTP)
                        .loadBalancer(productServiceProps.applicationLoadBalancer())
                        .build());

        FargateService fargateService = new FargateService(this, "ProductService", FargateServiceProps.builder()
                .serviceName("ProductService")
                .cluster(productServiceProps.cluster())
                .taskDefinition(fargateTaskDefinition)
                .desiredCount(2)
                // .assignPublicIp(true) -> .natGateways(0), we should use this property. Do not do this in production
                .build());

        productServiceProps.repository().grantPull(Objects.requireNonNull(fargateTaskDefinition.getExecutionRole()));
        fargateService.getConnections().getSecurityGroups().get(0).addIngressRule(Peer.anyIpv4(), Port.tcp(8080));

        applicationListener.addTargets("ProductServiceALBTarget", AddApplicationTargetsProps.builder()
                .targetGroupName("productServiceALB")
                .port(8080)
                .protocol(ApplicationProtocol.HTTP)
                .targets(Collections.singletonList(fargateService))
                .deregistrationDelay(Duration.seconds(30))
                .healthCheck(HealthCheck.builder()
                        .enabled(true)
                        .interval(Duration.seconds(30))
                        .timeout(Duration.seconds(10))
                        .path("/actuator/health")
                        .port("8080")
                        .build())
                .build());

        NetworkListener networkListener = productServiceProps.networkLoadBalancer()
                .addListener("ProductServiceNLBListener", BaseNetworkListenerProps.builder()
                        .port(8080)
                        .protocol(TCP)
                        .build());

        networkListener.addTargets("ProductServiceNLBTarget", AddNetworkTargetsProps.builder()
                .port(8080)
                .protocol(TCP)
                .targetGroupName("productServiceNLB")
                .targets(Collections.singletonList(fargateService.loadBalancerTarget(LoadBalancerTargetOptions.builder()
                        .containerName("productService")
                        .containerPort(8080)
                        .protocol(Protocol.TCP)
                        .build())))
                .build());
    }
}

record ProductServiceProps(
        Vpc vpc,
        Cluster cluster,
        NetworkLoadBalancer networkLoadBalancer,
        ApplicationLoadBalancer applicationLoadBalancer,
        Repository repository
) {
}
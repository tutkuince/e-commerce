package com.myorg;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.apigateway.*;
import software.amazon.awscdk.services.elasticloadbalancingv2.NetworkLoadBalancer;
import software.constructs.Construct;

public class APIStack extends Stack {

    public APIStack(
            final Construct scope,
            final String id,
            StackProps stackProps,
            APIStackProps apiStackProps) {
        super(scope, id, stackProps);

        RestApi restApi = new RestApi(this, "RestAPI", RestApiProps.builder()
                .restApiName("ECommerceAPI")
                .build());

        this.createProductResource(restApi, apiStackProps);

    }

    private void createProductResource(RestApi restApi, APIStackProps apiStackProps) {
        // /products
        Resource productResource = restApi.getRoot().addResource("products");
        // GET: /products
        productResource.addMethod("GET", new Integration(
                IntegrationProps.builder()
                        .type(IntegrationType.HTTP_PROXY)
                        .integrationHttpMethod("GET")
                        .uri("http://" + apiStackProps.networkLoadBalancer().getLoadBalancerDnsName() +
                                ":8080/api/products")
                        .options(IntegrationOptions.builder()
                                .vpcLink(apiStackProps.vpcLink())
                                .connectionType(ConnectionType.VPC_LINK)
                                .build())
                        .build()
        ));

        // POST /products
        productResource.addMethod("POST", new Integration(
                IntegrationProps.builder()
                        .type(IntegrationType.HTTP_PROXY)
                        .integrationHttpMethod("POST")
                        .uri("http://" + apiStackProps.networkLoadBalancer().getLoadBalancerDnsName() +
                                ":8080/api/products")
                        .options(IntegrationOptions.builder()
                                .vpcLink(apiStackProps.vpcLink())
                                .connectionType(ConnectionType.VPC_LINK)
                                .build())
                        .build()
        ));


        // GET /products/{id}

        // PUT /products/{id}

        // DELETE /products/{id}
    }
}

record APIStackProps(
        NetworkLoadBalancer networkLoadBalancer,
        VpcLink vpcLink
) {
}
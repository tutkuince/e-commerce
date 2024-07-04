package com.myorg;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.apigateway.RestApi;
import software.amazon.awscdk.services.apigateway.RestApiProps;
import software.amazon.awscdk.services.apigateway.VpcLink;
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

        
    }
}

record APIStackProps(
        NetworkLoadBalancer networkLoadBalancer,
        VpcLink vpcLink
) {
}
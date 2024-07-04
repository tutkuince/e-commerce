package com.myorg;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
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

        
    }
}

record APIStackProps(
        NetworkLoadBalancer networkLoadBalancer,
        VpcLink vpcLink
) {
}
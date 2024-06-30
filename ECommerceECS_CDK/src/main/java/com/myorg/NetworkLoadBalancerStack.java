package com.myorg;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.apigateway.VpcLink;
import software.amazon.awscdk.services.ec2.Vpc;
import software.amazon.awscdk.services.elasticloadbalancingv2.ApplicationLoadBalancer;
import software.amazon.awscdk.services.elasticloadbalancingv2.NetworkLoadBalancer;
import software.constructs.Construct;

public class NetworkLoadBalancerStack extends Stack {

    private final VpcLink vpcLink;
    private final NetworkLoadBalancer networkLoadBalancer;
    private final ApplicationLoadBalancer applicationLoadBalancer;

    public NetworkLoadBalancerStack(final Construct scope, final String id, final StackProps props, NetworkLoadBalancerStackProps networkLoadBalancerStackProps) {
        super(scope, id, props);
    }

    public VpcLink getVpcLink() {
        return vpcLink;
    }

    public NetworkLoadBalancer getNetworkLoadBalancer() {
        return networkLoadBalancer;
    }

    public ApplicationLoadBalancer getApplicationLoadBalancer() {
        return applicationLoadBalancer;
    }
}

record NetworkLoadBalancerStackProps(
        Vpc vpc
){}
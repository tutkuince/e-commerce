package com.myorg;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.apigateway.VpcLink;
import software.amazon.awscdk.services.apigateway.VpcLinkProps;
import software.amazon.awscdk.services.ec2.Vpc;
import software.amazon.awscdk.services.elasticloadbalancingv2.ApplicationLoadBalancer;
import software.amazon.awscdk.services.elasticloadbalancingv2.NetworkLoadBalancer;
import software.amazon.awscdk.services.elasticloadbalancingv2.NetworkLoadBalancerProps;
import software.constructs.Construct;

import java.util.Collections;

public class NetworkLoadBalancerStack extends Stack {

    private final VpcLink vpcLink;
    private final NetworkLoadBalancer networkLoadBalancer;
    private final ApplicationLoadBalancer applicationLoadBalancer;

    public NetworkLoadBalancerStack(final Construct scope, final String id, final StackProps props, NetworkLoadBalancerStackProps networkLoadBalancerStackProps) {
        super(scope, id, props);

        this.networkLoadBalancer = new NetworkLoadBalancer(this, "NLB", NetworkLoadBalancerProps.builder()
                .loadBalancerName("ECommerceNLB")
                .internetFacing(false)
                .vpc(networkLoadBalancerStackProps.vpc())
                .build());

        this.vpcLink = new VpcLink(this, "VPCLink", VpcLinkProps.builder()
                .targets(Collections.singletonList(this.networkLoadBalancer))
                .build());

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
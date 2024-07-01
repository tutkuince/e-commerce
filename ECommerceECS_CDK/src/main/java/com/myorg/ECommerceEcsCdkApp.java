package com.myorg;

import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;

import java.util.HashMap;
import java.util.Map;

public class ECommerceEcsCdkApp {
    public static void main(final String[] args) {
        App app = new App();

        Environment environment = Environment.builder()
                .account("211125673776")
                .region("eu-central-1")
                .build();

        Map<String, String> infraTags = new HashMap<>();
        infraTags.put("team", "NorthStar");
        infraTags.put("cost", "ECommerceInfra");


        StackProps stackProps = StackProps.builder()
                .env(environment)
                .tags(infraTags)
                .build();

        EcrStack ecrStack = new EcrStack(app, "Ecr", stackProps);

        VpcStack vpcStack = new VpcStack(app, "Vpc", stackProps);

        ClusterStack clusterStack = new ClusterStack(app, "Cluster", stackProps,
                new ClusterStackProps(vpcStack.getVpc()));
        clusterStack.addDependency(vpcStack);

        NetworkLoadBalancerStack networkLoadBalancerStack = new NetworkLoadBalancerStack(app, "NLB", stackProps,
                new NetworkLoadBalancerStackProps(vpcStack.getVpc()));
        networkLoadBalancerStack.addDependency(vpcStack);

        Map<String, String> productServiceTags = new HashMap<>();
        productServiceTags.put("team", "NorthStar");
        productServiceTags.put("cost", "ProductService");

        ProductServiceStack productServiceStack = new ProductServiceStack(app, "ProductService", StackProps.builder()
                .env(environment)
                .tags(productServiceTags)
                .build(),
                new ProductServiceProps(
                        vpcStack.getVpc(),
                        clusterStack.getCluster(),
                        networkLoadBalancerStack.getNetworkLoadBalancer(),
                        networkLoadBalancerStack.getApplicationLoadBalancer(),
                        ecrStack.getProductServiceRepository()
                )
        );
        productServiceStack.addDependency(vpcStack);
        productServiceStack.addDependency(clusterStack);
        productServiceStack.addDependency(networkLoadBalancerStack);
        productServiceStack.addDependency(ecrStack);

        app.synth();
    }
}


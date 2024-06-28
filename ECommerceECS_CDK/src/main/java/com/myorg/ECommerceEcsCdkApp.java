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


        EcrStack ecrStack = new EcrStack(app, "Ecr", StackProps.builder()
                .env(environment)
                .tags(infraTags)
                .build());

        VpcStack vpcStack = new VpcStack(app, "Vpc", StackProps.builder()
                .env(environment)
                .tags(infraTags)
                .build());

        app.synth();
    }
}


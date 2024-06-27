package com.myorg;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.constructs.Construct;

public class EcrStack extends Stack {

    public EcrStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);
    }
}

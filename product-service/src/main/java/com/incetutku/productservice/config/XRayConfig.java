package com.incetutku.productservice.config;

import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.AWSXRayRecorder;
import com.amazonaws.xray.AWSXRayRecorderBuilder;
import com.amazonaws.xray.jakarta.servlet.AWSXRayServletFilter;
import com.amazonaws.xray.strategy.sampling.CentralizedSamplingStrategy;
import jakarta.servlet.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.net.URL;

@Configuration
public class XRayConfig {
    private static final Logger LOG = LoggerFactory.getLogger(XRayConfig.class);

    private static final String SAMPLING_RULE_JSON = "classpath:xray/xray-sampling-rules.json";

    static {
        URL ruleFile = null;
        try {
            ruleFile = ResourceUtils.getURL(SAMPLING_RULE_JSON);
        } catch (FileNotFoundException e) {
            LOG.error("XRay config file not found");
        }

        AWSXRayRecorder awsxRayRecorder = AWSXRayRecorderBuilder.standard()
                .withDefaultPlugins()
                .withSamplingStrategy(new CentralizedSamplingStrategy(ruleFile))
                .build();

        AWSXRay.setGlobalRecorder(awsxRayRecorder);
    }

    @Bean
    public Filter tracingFilter() {
        return new AWSXRayServletFilter("product-service");
    }
}

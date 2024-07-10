package com.incetutku.productservice.config;

import com.incetutku.productservice.products.interceptor.ProductInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    private final ProductInterceptor productInterceptor;

    public InterceptorConfig(ProductInterceptor productInterceptor) {
        this.productInterceptor = productInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this.productInterceptor)
                .addPathPatterns("/api/products/**");
    }
}

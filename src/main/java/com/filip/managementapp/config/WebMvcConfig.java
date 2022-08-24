package com.filip.managementapp.config;

import com.filip.managementapp.config.resolver.ReactResourceResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.ResourceResolver;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        ResourceResolver resolver = new ReactResourceResolver();
        registry.addResourceHandler(SecurityConfig.WHITELISTED_REACT_ENDPOINTS)
                .resourceChain(true)
                .addResolver(resolver);
    }

}
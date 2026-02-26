package com.gateway.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfiguration {

    @Bean
    @Profile("production")
    public WebClient.Builder loadBalancedWebClientBuilderProduction() {
        return WebClient.builder();
    }

    @Bean
    @LoadBalanced
    @Profile("development")
    public WebClient.Builder loadBalancedWebClientBuilderDevelopment() {
        return WebClient.builder();
    }
}

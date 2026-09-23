package com.notification.configuration;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfiguration {

    @Bean
    @Profile("production")
    public RestClient.Builder loadBalancedRestClientBuilderProduction() {
        return RestClient.builder();
    }

    @Bean
    @LoadBalanced
    @Profile("development")
    public RestClient.Builder loadBalancedRestClientBuilderDevelopment() {
        return RestClient.builder();
    }
}

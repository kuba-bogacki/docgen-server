package com.company.configuration;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfiguration {

    @Bean
    @Primary
    @Profile(value = {"development", "production"})
    public RestClient.Builder restClientBuilderDevelopment() {
        return RestClient.builder();
    }

    @Bean
    @LoadBalanced
    @Profile(value = {"development", "production"})
    public RestClient.Builder loadBalancedRestClientBuilderDevelopment() {
        return RestClient.builder();
    }
}

package com.document;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
public class DocumentServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(DocumentServiceApplication.class, args);
	}
}

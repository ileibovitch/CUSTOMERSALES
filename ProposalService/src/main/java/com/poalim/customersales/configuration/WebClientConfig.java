package com.poalim.customersales.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${customerserver.url}") // assuming you have a property in your application.properties or application.yml for the customer service URL
    private String customerServiceUrl;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(customerServiceUrl) // set the base URL for the customer service
                .build();
    }
}

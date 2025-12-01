package com.backendtest.similar_products.infrastructure.out.http;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Configura el cliente HTTP utilizado para conversar con los mocks
 * externos que exponen los productos.
 */
@Configuration
public class ProductServiceClientConfig {

    @Bean
    public RestClient productRestClient(RestClient.Builder builder,
                                        @Value("${external.product-service.base-url}") String baseUrl) {
        return builder
                .baseUrl(baseUrl)
                .build();
    }
}

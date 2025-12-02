package com.backendtest.similar_products.infrastructure.out.http;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

/**
 * Configura el cliente HTTP utilizado para conversar con los mocks
 * externos que exponen los productos.
 */
@Configuration
public class ProductServiceClientConfig {

    @Bean
    public RestClient productRestClient(@Value("${external.product-service.base-url}") String baseUrl,
                                        @Value("${external.product-service.timeout.connect:500ms}") Duration connectTimeout,
                                        @Value("${external.product-service.timeout.read:1500ms}") Duration readTimeout) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout((int) connectTimeout.toMillis());
        requestFactory.setReadTimeout((int) readTimeout.toMillis());

        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();
    }
}

package com.backendtest.similar_products.infrastructure.out.http;

import com.backendtest.similar_products.domain.exception.ExternalServiceException;
import com.backendtest.similar_products.domain.exception.ProductNotFoundException;
import com.backendtest.similar_products.domain.model.Product;
import com.backendtest.similar_products.domain.port.ProductDetailPort;
import com.backendtest.similar_products.infrastructure.out.http.dto.ProductResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@Component
public class HttpProductDetailClient implements ProductDetailPort {

    private static final Logger log = LoggerFactory.getLogger(HttpProductDetailClient.class);

    private final RestClient productRestClient;

    public HttpProductDetailClient(@Qualifier("productRestClient") RestClient productRestClient) {
        this.productRestClient = productRestClient;
    }

    @Override
    @CircuitBreaker(name = "productService")
    @Retry(name = "productService")
    public Product findById(String productId) {
        try {
            ProductResponse response = productRestClient.get()
                    .uri("/product/{productId}", productId)
                    .retrieve()
                    .body(ProductResponse.class);

            if (response == null) {
                log.warn("External product service returned empty body for id {}", productId);
                throw new ProductNotFoundException(productId);
            }

            return response.toDomain();
        } catch (RestClientResponseException ex) {
            if (ex.getStatusCode().value() == HttpStatus.NOT_FOUND.value()) {
                log.warn("Product {} not found in external service", productId);
                throw new ProductNotFoundException(productId, ex);
            }
            log.error("External product service returned {} for id {}", ex.getStatusCode().value(), productId, ex);
            throw new ExternalServiceException("Error retrieving product " + productId, ex);
        } catch (RestClientException ex) {
            log.error("External product service error retrieving id {}", productId, ex);
            throw new ExternalServiceException("Error retrieving product " + productId, ex);
        }
    }
}

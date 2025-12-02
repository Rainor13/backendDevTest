package com.backendtest.similar_products.infrastructure.out.http;

import com.backendtest.similar_products.domain.exception.ExternalServiceException;
import com.backendtest.similar_products.domain.exception.ProductNotFoundException;
import com.backendtest.similar_products.domain.port.SimilarProductIdsPort;
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

import java.util.Arrays;
import java.util.List;

@Component
public class HttpSimilarProductIdsClient implements SimilarProductIdsPort {

    private static final Logger log = LoggerFactory.getLogger(HttpSimilarProductIdsClient.class);

    private final RestClient productRestClient;

    public HttpSimilarProductIdsClient(@Qualifier("productRestClient") RestClient productRestClient) {
        this.productRestClient = productRestClient;
    }

    @Override
    @CircuitBreaker(name = "productService")
    @Retry(name = "productService")
    public List<String> findSimilarIds(String productId) {
        try {
            String[] response = productRestClient.get()
                    .uri("/product/{productId}/similarids", productId)
                    .retrieve()
                    .body(String[].class);

            List<String> ids = response == null ? List.of() : Arrays.asList(response);
            if (ids.isEmpty()) {
                log.info("External product service returned no similar ids for {}", productId);
            }
            return ids;
        } catch (RestClientResponseException ex) {
            if (ex.getStatusCode().value() == HttpStatus.NOT_FOUND.value()) {
                log.warn("Similar ids not found for {}", productId);
                throw new ProductNotFoundException(productId, ex);
            }
            log.error("External product service returned {} while fetching similar ids for {}", ex.getStatusCode().value(), productId, ex);
            throw new ExternalServiceException("Error retrieving similar ids for product " + productId, ex);
        } catch (RestClientException ex) {
            log.error("External product service error fetching similar ids for {}", productId, ex);
            throw new ExternalServiceException("Error retrieving similar ids for product " + productId, ex);
        }
    }
}

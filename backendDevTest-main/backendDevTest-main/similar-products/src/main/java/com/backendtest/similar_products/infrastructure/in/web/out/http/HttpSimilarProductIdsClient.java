package com.codingtest.similar_products.infrastructure.out.http;

import com.codingtest.similar_products.domain.exception.ExternalServiceException;
import com.codingtest.similar_products.domain.exception.ProductNotFoundException;
import com.codingtest.similar_products.domain.port.SimilarProductIdsPort;
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

    private final RestClient productRestClient;

    public HttpSimilarProductIdsClient(@Qualifier("productRestClient") RestClient productRestClient) {
        this.productRestClient = productRestClient;
    }

    @Override
    public List<String> findSimilarIds(String productId) {
        try {
            String[] response = productRestClient.get()
                    .uri("/product/{productId}/similarids", productId)
                    .retrieve()
                    .body(String[].class);

            return response == null ? List.of() : Arrays.asList(response);
        } catch (RestClientResponseException ex) {
            if (ex.getStatusCode().value() == HttpStatus.NOT_FOUND.value()) {
                throw new ProductNotFoundException(productId, ex);
            }
            throw new ExternalServiceException("Error retrieving similar ids for product " + productId, ex);
        } catch (RestClientException ex) {
            throw new ExternalServiceException("Error retrieving similar ids for product " + productId, ex);
        }
    }
}
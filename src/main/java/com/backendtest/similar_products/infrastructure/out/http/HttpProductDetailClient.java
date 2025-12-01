package com.backendtest.similar_products.infrastructure.out.http;

import com.backendtest.similar_products.domain.exception.ExternalServiceException;
import com.backendtest.similar_products.domain.exception.ProductNotFoundException;
import com.backendtest.similar_products.domain.model.Product;
import com.backendtest.similar_products.domain.port.ProductDetailPort;
import com.backendtest.similar_products.infrastructure.out.http.dto.ProductResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@Component
public class HttpProductDetailClient implements ProductDetailPort {

    private final RestClient productRestClient;

    public HttpProductDetailClient(@Qualifier("productRestClient") RestClient productRestClient) {
        this.productRestClient = productRestClient;
    }

    @Override
    public Product findById(String productId) {
        try {
            ProductResponse response = productRestClient.get()
                    .uri("/product/{productId}", productId)
                    .retrieve()
                    .body(ProductResponse.class);

            if (response == null) {
                throw new ProductNotFoundException(productId);
            }

            return response.toDomain();
        } catch (RestClientResponseException ex) {
            if (ex.getStatusCode().value() == HttpStatus.NOT_FOUND.value()) {
                throw new ProductNotFoundException(productId, ex);
            }
            throw new ExternalServiceException("Error retrieving product " + productId, ex);
        } catch (RestClientException ex) {
            throw new ExternalServiceException("Error retrieving product " + productId, ex);
        }
    }
}

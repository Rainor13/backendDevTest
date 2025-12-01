package com.codingtest.similar_products.application;

import com.codingtest.similar_products.domain.model.Product;
import com.codingtest.similar_products.domain.port.ProductDetailPort;
import com.codingtest.similar_products.domain.port.SimilarProductIdsPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * Caso de uso encargado de recuperar los detalles de los
 * productos similares a uno dado.
 */
@Service
public class GetSimilarProductsUseCase {

    private final SimilarProductIdsPort similarProductIdsPort;
    private final ProductDetailPort productDetailPort;

    public GetSimilarProductsUseCase(SimilarProductIdsPort similarProductIdsPort,
                                     ProductDetailPort productDetailPort) {
        this.similarProductIdsPort = Objects.requireNonNull(similarProductIdsPort);
        this.productDetailPort = Objects.requireNonNull(productDetailPort);
    }

    public List<Product> execute(String productId) {
        if (productId == null || productId.isBlank()) {
            throw new IllegalArgumentException("Product id must not be blank");
        }
        return similarProductIdsPort.findSimilarIds(productId).stream()
                .map(productDetailPort::findById)
                .toList();
    }
}

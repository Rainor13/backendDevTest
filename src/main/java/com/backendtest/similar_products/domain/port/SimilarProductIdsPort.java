package com.backendtest.similar_products.domain.port;

import java.util.List;

/**
 * Puerto de salida que define cómo obtener los identificadores
 * de productos similares desde cualquier infraestructura externa.
 */
public interface SimilarProductIdsPort {

    List<String> findSimilarIds(String productId);
}

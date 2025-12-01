package com.codingtest.similar_products.domain.port;

import com.codingtest.similar_products.domain.model.Product;

/**
 * Puerto de salida responsable de recuperar el detalle de un
 * producto. La implementación concreta puede consultar un API
 * externo, base de datos, etc.
 */
public interface ProductDetailPort {

    Product findById(String productId);
}
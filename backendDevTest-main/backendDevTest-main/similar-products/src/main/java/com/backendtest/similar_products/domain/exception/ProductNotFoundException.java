package com.codingtest.similar_products.domain.exception;

/**
 * Excepción de dominio que indica que un producto requerido no existe
 * o no pudo ser recuperado del sistema externo.
 */
public class ProductNotFoundException extends RuntimeException {

    private final String productId;

    public ProductNotFoundException(String productId) {
        super("Product not found: " + productId);
        this.productId = productId;
    }

    public ProductNotFoundException(String productId, Throwable cause) {
        super("Product not found: " + productId, cause);
        this.productId = productId;
    }

    public String getProductId() {
        return productId;
    }
}
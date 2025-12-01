package com.backendtest.similar_products.infrastructure.out.http.dto;

import com.backendtest.similar_products.domain.model.Product;

import java.math.BigDecimal;

/**
 * Representa la forma en la que el servicio externo devuelve
 * el detalle de un producto. Se convierte a la entidad del dominio.
 */
public record ProductResponse(String id, String name, BigDecimal price, boolean availability) {

    public Product toDomain() {
        return new Product(id, name, price, availability);
    }
}

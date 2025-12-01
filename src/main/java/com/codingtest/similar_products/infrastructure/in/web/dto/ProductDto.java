package com.codingtest.similar_products.infrastructure.in.web.dto;

import com.codingtest.similar_products.domain.model.Product;

import java.math.BigDecimal;

public record ProductDto(String id, String name, BigDecimal price, boolean availability) {

    public static ProductDto fromDomain(Product product) {
        return new ProductDto(product.getId(), product.getName(), product.getPrice(), product.isAvailable());
    }
}

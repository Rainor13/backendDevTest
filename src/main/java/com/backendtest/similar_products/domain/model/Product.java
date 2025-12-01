package com.backendtest.similar_products.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Representa el agregado del dominio que describe un producto.
 * Es deliberadamente simple para mantener la lógica de negocio
 * aislada de frameworks o capas externas.
 */
public final class Product {

    private final String id;
    private final String name;
    private final BigDecimal price;
    private final boolean availability;

    public Product(String id, String name, BigDecimal price, boolean availability) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Product id must not be blank");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name must not be blank");
        }
        this.id = id;
        this.name = name;
        this.price = Objects.requireNonNull(price, "price must not be null");
        this.availability = availability;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public boolean isAvailable() {
        return availability;
    }
}

package com.backendtest.similar_products.infrastructure.in.web;

import com.backendtest.similar_products.application.GetSimilarProductsUseCase;
import com.backendtest.similar_products.infrastructure.in.web.dto.ProductDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final GetSimilarProductsUseCase getSimilarProductsUseCase;

    public ProductController(GetSimilarProductsUseCase getSimilarProductsUseCase) {
        this.getSimilarProductsUseCase = getSimilarProductsUseCase;
    }

    @GetMapping("/{productId}/similar")
    public List<ProductDto> getSimilarProducts(@PathVariable String productId) {
        return getSimilarProductsUseCase.execute(productId).stream()
                .map(ProductDto::fromDomain)
                .toList();
    }
}
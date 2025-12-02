package com.backendtest.similar_products.application;

import com.backendtest.similar_products.domain.exception.ProductNotFoundException;
import com.backendtest.similar_products.domain.model.Product;
import com.backendtest.similar_products.domain.port.ProductDetailPort;
import com.backendtest.similar_products.domain.port.SimilarProductIdsPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GetSimilarProductsUseCaseTest {

    private SimilarProductIdsPort similarProductIdsPort;
    private ProductDetailPort productDetailPort;
    private GetSimilarProductsUseCase useCase;

    @BeforeEach
    void setUp() {
        similarProductIdsPort = mock(SimilarProductIdsPort.class);
        productDetailPort = mock(ProductDetailPort.class);
        useCase = new GetSimilarProductsUseCase(similarProductIdsPort, productDetailPort);
    }

    @Test
    void shouldReturnProductsForValidProductId() {
        when(similarProductIdsPort.findSimilarIds("1")).thenReturn(List.of("2", "3"));
        when(productDetailPort.findById("2")).thenReturn(new Product("2", "Dress", BigDecimal.TEN, true));
        when(productDetailPort.findById("3")).thenReturn(new Product("3", "Boots", BigDecimal.ONE, false));

        List<Product> result = useCase.execute("1");

        assertEquals(2, result.size());
        assertEquals("2", result.get(0).getId());
        assertEquals("3", result.get(1).getId());
    }

    @Test
    void shouldPropagateDomainErrorWhenSimilarIdsFail() {
        when(similarProductIdsPort.findSimilarIds("1")).thenThrow(new ProductNotFoundException("1"));

        assertThrows(ProductNotFoundException.class, () -> useCase.execute("1"));
    }

    @Test
    void shouldValidateProductId() {
        assertThrows(IllegalArgumentException.class, () -> useCase.execute(" "));
        assertThrows(IllegalArgumentException.class, () -> useCase.execute(null));
    }

    @Test
    void shouldRequestDetailForEachSimilarId() {
        when(similarProductIdsPort.findSimilarIds("1")).thenReturn(List.of("2", "3"));
        when(productDetailPort.findById("2")).thenReturn(new Product("2", "Dress", BigDecimal.TEN, true));
        when(productDetailPort.findById("3")).thenReturn(new Product("3", "Boots", BigDecimal.ONE, false));

        useCase.execute("1");

        verify(productDetailPort).findById("2");
        verify(productDetailPort).findById("3");
    }
}

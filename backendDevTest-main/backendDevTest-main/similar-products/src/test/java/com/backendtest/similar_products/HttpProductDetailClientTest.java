package com.backendtest.similar_products.infrastructure.out.http;

import com.backendtest.similar_products.domain.exception.ExternalServiceException;
import com.backendtest.similar_products.domain.exception.ProductNotFoundException;
import com.backendtest.similar_products.domain.model.Product;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HttpProductDetailClientTest {

    private MockWebServer server;
    private HttpProductDetailClient client;

    @BeforeEach
    void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
        RestClient restClient = RestClient.builder()
                .baseUrl(server.url("/").toString())
                .build();
        client = new HttpProductDetailClient(restClient);
    }

    @AfterEach
    void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    void shouldMapResponseToDomainProduct() throws InterruptedException {
        server.enqueue(new MockResponse()
                .setBody("""
                        {
                          "id": "1",
                          "name": "Dress",
                          "price": 10.5,
                          "availability": true
                        }
                        """)
                .addHeader("Content-Type", "application/json"));

        Product product = client.findById("1");

        assertEquals("1", product.getId());
        assertEquals("Dress", product.getName());
        assertEquals(new BigDecimal("10.5"), product.getPrice());
        assertEquals(true, product.isAvailable());

        RecordedRequest request = server.takeRequest();
        assertEquals("/product/1", request.getPath());
    }

    @Test
    void shouldTranslateNotFoundToDomainException() {
        server.enqueue(new MockResponse().setResponseCode(404));

        assertThrows(ProductNotFoundException.class, () -> client.findById("999"));
    }

    @Test
    void shouldTranslateServerErrors() {
        server.enqueue(new MockResponse().setResponseCode(500));

        assertThrows(ExternalServiceException.class, () -> client.findById("1"));
    }
}
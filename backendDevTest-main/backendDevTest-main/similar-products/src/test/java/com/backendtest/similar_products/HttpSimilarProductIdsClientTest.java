package com.backendtest.similar_products.infrastructure.out.http;

import com.backendtest.similar_products.domain.exception.ExternalServiceException;
import com.backendtest.similar_products.domain.exception.ProductNotFoundException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HttpSimilarProductIdsClientTest {

    private MockWebServer server;
    private HttpSimilarProductIdsClient client;

    @BeforeEach
    void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
        RestClient restClient = RestClient.builder()
                .baseUrl(server.url("/").toString())
                .build();
        client = new HttpSimilarProductIdsClient(restClient);
    }

    @AfterEach
    void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    void shouldReturnSimilarIds() throws InterruptedException {
        server.enqueue(new MockResponse()
                .setBody("[\"2\",\"3\"]")
                .addHeader("Content-Type", "application/json"));

        List<String> ids = client.findSimilarIds("1");

        assertEquals(List.of("2", "3"), ids);

        RecordedRequest request = server.takeRequest();
        assertEquals("/product/1/similarids", request.getPath());
    }

    @Test
    void shouldReturnEmptyListWhenBodyIsNull() {
        server.enqueue(new MockResponse()
                .setBody("")
                .addHeader("Content-Type", "application/json"));

        List<String> ids = client.findSimilarIds("1");

        assertEquals(List.of(), ids);
    }

    @Test
    void shouldThrowWhenNotFound() {
        server.enqueue(new MockResponse().setResponseCode(404));

        assertThrows(ProductNotFoundException.class, () -> client.findSimilarIds("999"));
    }

    @Test
    void shouldThrowExternalServiceExceptionOnServerError() {
        server.enqueue(new MockResponse().setResponseCode(500));

        assertThrows(ExternalServiceException.class, () -> client.findSimilarIds("1"));
    }
}
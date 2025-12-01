package com.codingtest.similar_products.domain.exception;

/**
 * Excepción que representa fallos genéricos al comunicarse con
 * servicios externos.
 */
public class ExternalServiceException extends RuntimeException {

    public ExternalServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}

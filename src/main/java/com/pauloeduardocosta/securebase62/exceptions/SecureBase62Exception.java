package com.pauloeduardocosta.securebase62.exceptions;

/**
 * Exception specific to SecureBase62 encoding/decoding errors.
 */
public class SecureBase62Exception extends RuntimeException {

    private static final long serialVersionUID = -8164737120303758030L;

    /**
     * Creates a new exception with the specified message.
     *
     * @param message The error message
     */
    public SecureBase62Exception(final String message) {
        super(message);
    }

    /**
     * Creates a new exception with the specified message and cause.
     *
     * @param message The error message
     * @param cause The cause of the exception
     */
    public SecureBase62Exception(final String message, final Throwable cause) {
        super(message, cause);
    }
}

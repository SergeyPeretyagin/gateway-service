package com.gatewayserver.exception;

import org.springframework.security.core.AuthenticationException;

public class TokenException extends RuntimeException {
    public TokenException(String message) {
        super(message);
    }
}

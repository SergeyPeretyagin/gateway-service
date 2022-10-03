package com.gatewayserver.service;

import com.gatewayserver.DTO.TokenDto;
import org.springframework.web.server.ServerWebExchange;
import java.util.UUID;


public interface AuthService {


    TokenDto getToken(UUID userId, String requestPassword, String responsePassword);

    String getUriInstance();

    UUID getUUIDFromToken(String refreshToken, ServerWebExchange exchange);
    TokenDto getTokenWithoutPasswords(UUID userId);

    TokenDto getNewTokenAccess(String userId,ServerWebExchange exchange);


}

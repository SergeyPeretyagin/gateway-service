package com.gatewayserver.service;

import com.gatewayserver.DTO.RequestUserDto;
import com.gatewayserver.DTO.TokenDto;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public interface ConnectService {

    Mono<TokenDto> getTokenDto(RequestUserDto requestUserDto);

}

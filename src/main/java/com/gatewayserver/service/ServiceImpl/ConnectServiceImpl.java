package com.gatewayserver.service.ServiceImpl;

import com.gatewayserver.DTO.RequestUserDto;
import com.gatewayserver.DTO.ResponseLoginDto;
import com.gatewayserver.DTO.TokenDto;
import com.gatewayserver.DTO.UserDto;
import com.gatewayserver.exception.BadRequestException;
import com.gatewayserver.service.AuthService;
import com.gatewayserver.service.ConnectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConnectServiceImpl implements ConnectService {
    private final AuthService authService;
    private final WebClient webClient;
    @Override
    public Mono<TokenDto> getTokenDto(RequestUserDto requestUserDto){
        return webClient
                .post()
                .uri(authService.getUriInstance())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(new UserDto(requestUserDto.getLogin(), requestUserDto.getPassword()))
                .retrieve()
                .bodyToMono(ResponseLoginDto.class)
                .map(responseLoginDto -> {
                    log.info("request password {}, response password {} ",requestUserDto.getPassword(),responseLoginDto.getPassword());
                    return authService.getToken(responseLoginDto.getId(),requestUserDto.getPassword(),responseLoginDto.getPassword());
                });
    }
}

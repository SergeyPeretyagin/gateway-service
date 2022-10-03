package com.gatewayserver.exception;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.handler.WebFluxResponseStatusExceptionHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.nio.charset.StandardCharsets;


import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
@Order(-2)
@Slf4j
public class RestResponseEntityExceptionHandler extends WebFluxResponseStatusExceptionHandler {

    @Override
    @ExceptionHandler(ExpiredJwtException.class)
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        System.out.println(ex.getMessage());
        if (ex.getMessage().contains("time")) {
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(UNAUTHORIZED);
            response.setRawStatusCode(401);
            DefaultDataBufferFactory factory = new DefaultDataBufferFactory();
            DataBuffer buffer = factory.wrap("Token expired".getBytes(StandardCharsets.UTF_8));
            return exchange.getResponse().writeWith(Mono.just(buffer));
        } else if (ex.getMessage().contains("signature")) {
            exchange.getResponse().setStatusCode(BAD_REQUEST);
            exchange.getResponse().setRawStatusCode(400);
            DefaultDataBufferFactory factory = new DefaultDataBufferFactory();
            DataBuffer buffer = factory.wrap("Invalid data".getBytes(StandardCharsets.UTF_8));
            return exchange.getResponse().writeWith(Mono.just(buffer));
        }
        else if (ex.getMessage().contains("400 BAD_REQUEST")||ex.getMessage().contains("Validation")){
            exchange.getResponse().setStatusCode(BAD_REQUEST);
            exchange.getResponse().setRawStatusCode(400);
            DefaultDataBufferFactory factory = new DefaultDataBufferFactory();
            DataBuffer buffer = factory.wrap("Invalid data".getBytes(StandardCharsets.UTF_8));
            return exchange.getResponse().writeWith(Mono.just(buffer));
        }
        return Mono.empty();
    }
}

package com.gatewayserver.componenet;

import com.gatewayserver.Util.HelpingUtil;
import com.gatewayserver.security.JwtProvider;
import io.jsonwebtoken.MalformedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DefaultDataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.nio.ByteBuffer;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component
@Slf4j
public class FilterJwt extends AbstractGatewayFilterFactory<FilterJwt.Config> {

    @Autowired
    private JwtProvider jwtProvider;

    public FilterJwt(JwtProvider jwtProvider) {
        super(Config.class);
        this.jwtProvider = jwtProvider;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            if (this.isAuthMissing(request)) {
                log.error("Authorization header is missing in request");
                return this.onError(exchange, "Invalid data", HttpStatus.BAD_REQUEST);
            }
            String withoutBearer = jwtProvider.resolveToken(exchange);
            if (withoutBearer.equals("No found Bearer")) {
                return this.onError(exchange, "Invalid data", HttpStatus.BAD_REQUEST);
            }
            String uuid;
            try {
                uuid = jwtProvider.getUserId(withoutBearer);
            } catch (MalformedJwtException e) {
                log.error("Bad request with token {} ", e.getMessage());
                return this.onError(exchange, "Invalid data", HttpStatus.BAD_REQUEST);
            }
            log.info("uuid {} ", uuid);
            try {
                if (!jwtProvider.validationToken(withoutBearer)) {
                    log.error("validationToken. Authorization header is invalid");
                    return this.onError(exchange, "Authorization header is invalid", HttpStatus.UNAUTHORIZED);
                }
            } catch (RuntimeException e) {
                log.error("RuntimeException Authorization header is invalid");
                return this.onError(exchange, "Authorization header is invalid", HttpStatus.UNAUTHORIZED);
            }
            String userId = request.getQueryParams().getFirst("clientId");
            log.info("userId from request {}", userId);
            if (userId != null && !HelpingUtil.checkIdFromToken(userId, uuid)) {
                log.error("checkToken. Authorization header is invalid");
                return this.onError(exchange, "invalid data", HttpStatus.BAD_REQUEST);
            }
            return chain.filter(exchange);
        });
    }


    private String getAuthHeader(ServerHttpRequest request) {
        return request.getHeaders().getOrEmpty("Authorization").get(0);
    }

    private boolean isAuthMissing(ServerHttpRequest request) {
        return !request.getHeaders().containsKey("Authorization");
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        ByteBuffer byteBuffer = ByteBuffer.wrap(err.getBytes(UTF_8));
        DefaultDataBuffer dataBuffer = new DefaultDataBufferFactory().wrap(byteBuffer);
        return response.writeWith(Flux.just(dataBuffer));
    }


    public static class Config{}
}

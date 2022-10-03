package com.gatewayserver.security;

import com.gatewayserver.Util.HelpingUtil;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;


@Component
@RequiredArgsConstructor
@Slf4j
public class JwtProvider {

    private String secret = "secret";

    private Long expiration = 600000L;

    public static final String BEARER = "Bearer ";

    @PostConstruct
    void init() {
        secret = Base64.getEncoder().encodeToString(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(UUID userId) {
        log.info("JwtProvider class. createToken method.{} it works {} ",userId, new Date().getTime() + expiration);
//        Claims claims = Jwts.claims().setSubject(String.valueOf(userId));
        Date expirationDate = new Date(new Date().getTime() + expiration);
        return Jwts.builder()
//                .setClaims(claims)
                .setSubject(String.valueOf(userId))
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }
    public String createRefreshToken(UUID userId){
        log.info("JwtProvider class. createRefreshToken method. {} it works {} ",userId, new Date().getTime() + 3*expiration);
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setExpiration(new Date(new Date().getTime() + (3*expiration)))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }
    public String getUserId(String token) throws MalformedJwtException{
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validationToken(String token){
        log.info("JwtProvider class. validationToken method {} ",token);
        Jws<Claims> claimsJws =  Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
        if (claimsJws.getBody().getExpiration().before(new Date())){
            return false;
        }
        return true;
    }

    public String  resolveToken (ServerWebExchange exchange){
        log.info("JwtProvider. ResolveToken method {} ", exchange.getRequest().getHeaders());
        String bearerToken = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (HelpingUtil.checkTokenContainsBearer(bearerToken)) {
            log.info("JwtProvider class. return from resolveToken method {} ",bearerToken.substring(BEARER.length()));
            return bearerToken.substring(BEARER.length());
        }
        log.error("JwtProvider. ResolveToken method. No found 'Bearer'");
        return "No found Bearer";
    }

}

package com.gatewayserver.service.ServiceImpl;

import com.gatewayserver.DTO.TokenDto;
import com.gatewayserver.Util.HelpingUtil;
import com.gatewayserver.exception.BadRequestException;
import com.gatewayserver.security.JwtProvider;
import com.gatewayserver.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import java.net.URI;
import java.util.UUID;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {
    @Autowired
    private DiscoveryClient client;
    @Autowired
    private JwtProvider jwtProvider;

    public TokenDto getToken(UUID userId, String requestPassword, String responsePassword) {
        if (HelpingUtil.checkPassword(requestPassword, responsePassword)) {
            return new TokenDto(jwtProvider.createToken(userId),
                    jwtProvider.createRefreshToken(userId));
        } else {
            log.error("BadRequestException: " + requestPassword + " Incorrect password");
            throw new BadRequestException("Invalid data");
        }
    }

    public String getUriInstance() {
        String endPointServiceUser = HelpingUtil.getEndPointToServiceUser();
        URI uriServiceUser = client.getInstances("serviceuser").get(0).getUri();
        return String.valueOf(uriServiceUser)+endPointServiceUser;

    }

    public TokenDto getTokenWithoutPasswords(UUID userId) {
        return new TokenDto(jwtProvider.createToken(userId),
                jwtProvider.createRefreshToken(userId));
    }
    @Override
    public TokenDto getNewTokenAccess(String refreshToken, ServerWebExchange exchange) {
        UUID userId = getUUIDFromToken(refreshToken,exchange);
        return getTokenWithoutPasswords(userId);
    }

    public UUID getUUIDFromToken(String refreshToken, ServerWebExchange exchange) {
        log.info("AuthServiceImpl. getNewAccessToken method {} ", refreshToken);
        String tokenWithoutBearer = jwtProvider.resolveToken(exchange);
        if (jwtProvider.validationToken(tokenWithoutBearer)) {
            return UUID.fromString(jwtProvider.getUserId(tokenWithoutBearer));
        } else {
            throw new BadRequestException("Invalid data");
        }
    }
}

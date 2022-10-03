package com.gatewayserver.serviceTest;

import com.gatewayserver.DTO.TokenDto;
import com.gatewayserver.exception.BadRequestException;
import com.gatewayserver.mapper.TestCreator;
import com.gatewayserver.security.JwtProvider;
import com.gatewayserver.service.ServiceImpl.AuthServiceImpl;
import io.jsonwebtoken.MalformedJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.UUID;

import static com.gatewayserver.security.JwtProvider.BEARER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @InjectMocks
    private AuthServiceImpl mockService;
    @Mock
    private JwtProvider mockProvider;

    private TokenDto tokenDto;
    @BeforeEach
    void setUp(){
        tokenDto = TestCreator.getTokenDto();
    }

    @ParameterizedTest(name = "#{index} - Run test with requestUserDto = {0}")
    @MethodSource("com.gatewayserver.mapper.TestCreator#streamArgumentsWithValidPasswords")
    void testGetTokenSuccess(UUID userId, String requestPassword, String responsePassword){
        lenient().when(mockProvider.createToken(userId)).thenReturn(tokenDto.getAccessToken());
        lenient().when(mockProvider.createRefreshToken(userId)).thenReturn(tokenDto.getRefreshToken());
        TokenDto expectedTdo = mockService.getToken(userId,requestPassword,responsePassword);
        assertEquals(tokenDto,expectedTdo);
        verify(mockProvider,times(1)).createToken(userId);
        verify(mockProvider,times(1)).createRefreshToken(userId);
    }

    @ParameterizedTest(name = "#{index} - Run test with requestUserDto = {0}")
    @MethodSource("com.gatewayserver.mapper.TestCreator#streamArgumentsWithNotValidPasswords")
    void testGetTokenSuccessNotSuccessWithNotEqualsPasswords(UUID userId, String requestPassword, String responsePassword){
        assertThrows(BadRequestException.class, ()->mockService.getToken(userId,requestPassword,responsePassword));
    }
    @ParameterizedTest(name = "#{index} - Run test with userID = {0}")
    @MethodSource("com.gatewayserver.mapper.TestCreator#streamUUID")
    void testGetTokenWithoutPasswords(UUID userID){
        when(mockProvider.createToken(userID)).thenReturn(tokenDto.getAccessToken());
        when(mockProvider.createRefreshToken(userID)).thenReturn(tokenDto.getRefreshToken());
        TokenDto expectedTdo = mockService.getTokenWithoutPasswords(userID);
        assertEquals(tokenDto,expectedTdo);
        verify(mockProvider,times(1)).createToken(userID);
        verify(mockProvider,times(1)).createRefreshToken(userID);
    }

    @Test
    void testGetUUIDFromTokenSuccess(){
        UUID userId = TestCreator.getUserId();
        String refreshToken = tokenDto.getRefreshToken();
        String withoutBearer = refreshToken.substring(BEARER.length());
        MockServerWebExchange exchange = MockServerWebExchange
                .builder(MockServerHttpRequest.method(HttpMethod.GET, "/api/v1/login/token")
                        .header(HttpHeaders.AUTHORIZATION, refreshToken))
                .build();
        when(mockProvider.resolveToken(exchange)).thenReturn(withoutBearer);
        when(mockProvider.validationToken(withoutBearer)).thenReturn(true);
        when(mockProvider.getUserId(withoutBearer)).thenReturn(String.valueOf(userId));
        UUID expectedId = mockService.getUUIDFromToken(refreshToken,exchange);

        assertEquals(userId,expectedId);
        verify(mockProvider,times(1)).resolveToken(exchange);
        verify(mockProvider,times(1)).validationToken(withoutBearer);
        verify(mockProvider,times(1)).getUserId(withoutBearer);
    }
    @Test
    void testGetUUIDFromTokenNotSuccessWithTokenExpired(){
        String refreshToken = tokenDto.getRefreshToken();
        MockServerWebExchange exchange = MockServerWebExchange
                .builder(MockServerHttpRequest.method(HttpMethod.GET, "/api/v1/login/token")
                        .header(HttpHeaders.AUTHORIZATION, refreshToken))
                .build();
        when(mockProvider.resolveToken(exchange)).thenThrow(new BadRequestException("Token Expired"));
        assertThrows(BadRequestException.class,()->mockService.getUUIDFromToken(refreshToken,exchange));
        verify(mockProvider,times(1)).resolveToken(exchange);
    }
    @Test
    void testGetUUIDFromTokenNotSuccessWithNotValidToken(){
        String oldToken = TestCreator.getOldToken();
        MockServerWebExchange exchange = MockServerWebExchange
                .builder(MockServerHttpRequest.method(HttpMethod.GET, "/api/v1/login/token")
                        .header(HttpHeaders.AUTHORIZATION, oldToken))
                .build();
        assertThrows(BadRequestException.class,()->when(mockService.getUUIDFromToken(oldToken,exchange))
                .thenThrow(new BadRequestException("Invalid data")));
    }

    @Test
    void testGetNewAccessNotSuccessIfTokenExpired(){
        String notValidToken = TestCreator.getNotValidToken();
        String withoutBearer = notValidToken.substring(BEARER.length());
        MockServerWebExchange exchange = MockServerWebExchange
                .builder(MockServerHttpRequest.method(HttpMethod.GET, "/api/v1/login/token")
                        .header(HttpHeaders.AUTHORIZATION, notValidToken))
                .build();
        when(mockProvider.resolveToken(exchange)).thenReturn(withoutBearer);
        when(mockProvider.validationToken(withoutBearer)).thenThrow(new MalformedJwtException("Token expired"));
        Throwable throwable = catchThrowable(()->{
            mockService.getNewTokenAccess(notValidToken,exchange);
        });
        assertThat(throwable).isInstanceOf(MalformedJwtException.class);

    }

    @Test
    void testGetNewAccessNotSuccessIfTokenNotValid(){
        String notValidToken = TestCreator.getNotValidToken();
        MockServerWebExchange exchange = MockServerWebExchange
                .builder(MockServerHttpRequest.method(HttpMethod.GET, "/api/v1/login/token")
                        .header(HttpHeaders.AUTHORIZATION, notValidToken))
                .build();
        Throwable throwable = catchThrowable(()->{
            mockService.getNewTokenAccess(notValidToken,exchange);
        });
        assertThat(throwable).isInstanceOf(BadRequestException.class);
    }
}

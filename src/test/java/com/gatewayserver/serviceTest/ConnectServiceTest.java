package com.gatewayserver.serviceTest;

import com.gatewayserver.DTO.RequestUserDto;
import com.gatewayserver.DTO.ResponseLoginDto;
import com.gatewayserver.DTO.TokenDto;
import com.gatewayserver.DTO.UserDto;
import com.gatewayserver.exception.BadRequestException;
import com.gatewayserver.mapper.TestCreator;
import com.gatewayserver.service.AuthService;
import com.gatewayserver.service.ServiceImpl.ConnectServiceImpl;
import io.jsonwebtoken.MalformedJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ConnectServiceTest {
    @Mock
    private WebClient webClient;
    @Mock
    private AuthService authService;
    @Mock
    WebClient.RequestBodyUriSpec requestBodyUriSpec;
    @Mock
    WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock
    WebClient.RequestBodySpec requestBodySpec;
    @Mock
    WebClient.ResponseSpec responseSpec;
    @Mock
    private ResponseLoginDto mockResponse;
    @Mock
    private RequestUserDto mockRequest;
    @Mock
    private ServerWebExchange mockExchange;
    private ConnectServiceImpl connectService;

    private TokenDto actualTokenDto;

    private String refreshToken;
    private UUID actualUserId;

    @BeforeEach
    void setUp(){
        connectService = new ConnectServiceImpl(authService,webClient);
        refreshToken = TestCreator.getTokenDto().getRefreshToken();
        actualUserId = TestCreator.getUserId();
        actualTokenDto = TestCreator.getTokenDto();

    }

    @Test
    void testGetTokenDtoSuccess(){
        when(authService.getToken(mockResponse.getId(),mockRequest.getPassword(),mockResponse.getPassword())).thenReturn(actualTokenDto);
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(authService.getUriInstance())).thenReturn(requestBodySpec);
        when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(new UserDto(mockRequest.getLogin(),mockRequest.getPassword()))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ResponseLoginDto.class)).thenReturn(Mono.just(mockResponse));

        Mono<TokenDto> expectedTokenDTO = connectService.getTokenDto(mockRequest)
                .map(e->{
                    return authService.getToken(mockResponse.getId(),mockRequest.getPassword(),mockResponse.getPassword());
                });

        StepVerifier.create(expectedTokenDTO)
                .expectNextMatches(expect -> expect.equals(actualTokenDto))
                .verifyComplete();
    }

    @Test
    void testGetTokenDtoNotSuccessWithNotEqualsPassword(){
        when(authService.getToken(mockResponse.getId(),mockRequest.getPassword(),mockResponse.getPassword()))
                .thenThrow(new BadRequestException("Invalid data"));
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(authService.getUriInstance())).thenReturn(requestBodySpec);
        when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(new UserDto(mockRequest.getLogin(),mockRequest.getPassword()))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ResponseLoginDto.class)).thenReturn(Mono.just(mockResponse));

        Mono<TokenDto> expectedTokenTDO = connectService.getTokenDto(mockRequest)
                .map(e->{
                    return authService.getToken(mockResponse.getId(),mockRequest.getPassword(),mockResponse.getPassword());
                });

        StepVerifier.create(expectedTokenTDO)
                .expectErrorMatches(throwable -> throwable instanceof BadRequestException &&
                throwable.getMessage().equals("Invalid data")).verify();
    }

    @Test
    void testGetTokenDtoNotSuccessIfServerThrowException(){
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(authService.getUriInstance())).thenReturn(requestBodySpec);
        when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(new UserDto(mockRequest.getLogin(),mockRequest.getPassword()))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ResponseLoginDto.class)).thenThrow(new BadRequestException("Invalid data"));

        Throwable throwable = catchThrowable(()->{
            connectService.getTokenDto(mockRequest);
        });
        assertThat(throwable).isInstanceOf(BadRequestException.class);
    }

}

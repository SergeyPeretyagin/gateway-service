package com.gatewayserver.controllerTest;

import com.gatewayserver.DTO.RequestUserDto;
import com.gatewayserver.DTO.TokenDto;
import com.gatewayserver.controller.AuthController;
import com.gatewayserver.exception.BadRequestException;
import com.gatewayserver.mapper.TestCreator;
import com.gatewayserver.service.ServiceImpl.AuthServiceImpl;
import com.gatewayserver.service.ServiceImpl.ConnectServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import java.util.concurrent.ExecutionException;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;


@ExtendWith(MockitoExtension.class)
@WebFluxTest(controllers = AuthController.class,excludeAutoConfiguration = {ReactiveSecurityAutoConfiguration.class})
public class AuthControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ConnectServiceImpl connectServiceImpl;

    @MockBean
    private AuthServiceImpl authService;

    private TokenDto tokenDto;


    @BeforeEach
    void setUp(){
        tokenDto= TestCreator.getTokenDto();
    }


    @ParameterizedTest(name = "#{index} - Run test with requestUserDto = {0}")
    @MethodSource("com.gatewayserver.mapper.TestCreator#streamValidRequestUserDto")
    void testGetTokenSuccess(RequestUserDto requestUserDto) {
        when(connectServiceImpl.getTokenDto(requestUserDto)).thenReturn(Mono.just(tokenDto));
        webTestClient.mutateWith(csrf()).post()
                .uri("/api/v1/login")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(requestUserDto), RequestUserDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TokenDto.class)
                .isEqualTo(tokenDto);
        verify(connectServiceImpl,times(1)).getTokenDto(requestUserDto);
    }

    @ParameterizedTest(name = "#{index} - Run test with requestUserDto = {0}")
    @MethodSource("com.gatewayserver.mapper.TestCreator#streamRequestUserDtoWithNotValidLogin")
    void testGetTokenNotSuccessWithNoValidLogin(RequestUserDto requestUserDto) {
        webTestClient.mutateWith(csrf()).post()
                .uri("/api/v1/login")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(requestUserDto), RequestUserDto.class)
                .exchange()
                .expectStatus().isBadRequest();
        verify(connectServiceImpl,times(0)).getTokenDto(requestUserDto);
    }

    @ParameterizedTest(name = "#{index} - Run test with requestUserDto = {0}")
    @MethodSource("com.gatewayserver.mapper.TestCreator#streamRequestUserDtoWithNotValidPassword")
    void testGetTokenNotSuccessWithNoValidPassword(RequestUserDto requestUserDto) {
        webTestClient.mutateWith(csrf()).post()
                .uri("/api/v1/login")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(requestUserDto), RequestUserDto.class)
                .exchange()
                .expectStatus().isBadRequest();
        verify(connectServiceImpl,times(0)).getTokenDto(requestUserDto);
    }

    @ParameterizedTest(name = "#{index} - Run test with requestUserDto = {0}")
    @MethodSource("com.gatewayserver.mapper.TestCreator#streamValidRequestUserDto")
    void testGetTokenNotSuccessIfUserNotExist(RequestUserDto requestUserDto) {
        when(connectServiceImpl.getTokenDto(requestUserDto)).thenThrow(new BadRequestException("Invalid data"));
        webTestClient.mutateWith(csrf()).post()
                .uri("/api/v1/login")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(requestUserDto), RequestUserDto.class)
                .exchange()
                .expectStatus().isBadRequest();
        verify(connectServiceImpl,times(1)).getTokenDto(requestUserDto);
    }

    @Test
    void testGetNewTokenSuccess() {
        String refreshToken = anyString();
        when(authService.getNewTokenAccess(refreshToken,any())).thenReturn(tokenDto);
        webTestClient.get()
                .uri("/api/v1/login/token")
                .header(HttpHeaders.AUTHORIZATION, refreshToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TokenDto.class)
                .isEqualTo(tokenDto);
        verify(authService,times(1)).getNewTokenAccess(anyString(),any());
    }

    @Test
    void testGetNewTokenNotSuccessWithInvalidToken()  {
        String refreshToken = anyString();
        when(authService.getNewTokenAccess(refreshToken,any())).thenThrow(new BadRequestException("Invalid data"));
        webTestClient.get()
                .uri("/api/v1/login/token")
                .header(HttpHeaders.AUTHORIZATION, refreshToken)
                .exchange()
                .expectStatus().isBadRequest();
        verify(authService,times(1)).getNewTokenAccess(anyString(),any());
    }

    @Test
    void testGetNewTokenNotSuccessWithTokenExpired() {
        String refreshToken = anyString();
        when(authService.getNewTokenAccess(refreshToken,any())).thenThrow(new UsernameNotFoundException("Token expired"));
        webTestClient.get()
                .uri("/api/v1/login/token")
                .header(HttpHeaders.AUTHORIZATION, refreshToken)
                .exchange()
                .expectStatus().isUnauthorized();
        verify(authService,times(1)).getNewTokenAccess(anyString(),any());
    }

}

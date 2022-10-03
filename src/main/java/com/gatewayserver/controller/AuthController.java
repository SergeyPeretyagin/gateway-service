package com.gatewayserver.controller;

import com.gatewayserver.DTO.RequestUserDto;
import com.gatewayserver.DTO.TokenDto;
import com.gatewayserver.exception.BadRequestException;
import com.gatewayserver.service.AuthService;
import com.gatewayserver.service.ServiceImpl.ConnectServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.callbacks.Callback;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import javax.validation.Valid;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping(path = "/api/v1/login")
@Tag(name="Authenticate controller", description="This controller is designed to authorize," +
        " receive and update the access token")
public class AuthController {

    @Autowired
    private ConnectServiceImpl connectServiceImpl;
    @Autowired
    private AuthService authService;

    @Operation(
            summary = "Client authorization",
            description = "This endpoint allows you to get an access token"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TokenDto.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid data/Login not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content)})
    @PostMapping()
    public Mono<TokenDto> getToken(@Valid @RequestBody RequestUserDto requestUserDto) {
        log.info("AuthController. {} getToken method {} ",requestUserDto.getLogin(), requestUserDto.getPassword());
        return connectServiceImpl.getTokenDto(requestUserDto);
    }

    @Operation(
            summary = "Re-obtaining the access token",
            description = "You need to insert a refresh token in header Authorization", parameters = {}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TokenDto.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid data",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Token expired",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content)})

    @GetMapping(path = "/token")
    public Mono<TokenDto> getNewToken(@Parameter(in = ParameterIn.HEADER, name = "Authorization",
            description = "You need to enter Bearer and refresh token")
                                      @RequestHeader(name = "Authorization") String refreshToken,
                                      ServerWebExchange exchange) {
        log.info("AuthController, getNewToken method {} ", refreshToken);
        return Mono.just(authService.getNewTokenAccess(refreshToken, exchange));
    }
}

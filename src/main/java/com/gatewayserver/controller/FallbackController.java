package com.gatewayserver.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;


@RestController
@Hidden
public class FallbackController {

    @RequestMapping("/login")
    public Mono<String> loginControllerFallback(){
        return Mono.just("User service is taking too long to respond or is down. Please try again later");
    }

    @RequestMapping("/registration")
    public Mono<String> registrationControllerFallback(){
        return Mono.just("User service is taking too long to respond or is down. Please try again later");
    }

    @RequestMapping("/security")
    public Mono<String> securityControllerFallback(){
        return Mono.just("User service is taking too long to respond or is down. Please try again later");
    }

    @RequestMapping("/auth/user/settings")
    public Mono<String> settingControllerFallback(){
        return Mono.just("User service is taking too long to respond or is down. Please try again later");
    }
}

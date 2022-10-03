package com.gatewayserver.mapper;

import com.gatewayserver.DTO.RequestUserDto;
import com.gatewayserver.DTO.ResponseLoginDto;
import com.gatewayserver.DTO.TokenDto;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class TestCreator {
    public static RequestUserDto getRequestUserDTO (){
        return new RequestUserDto("123456789012","qwe123WWW");
    }

    public static String getOldToken(){
       return "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI0YmEwNzU5OC0xYzEyLTRlYTgtYTY2OS0wY2VlOWQ4M2I3YmEiLCJleHAiOjE2NTc0NDU2NDN9.I7LZr12pskH7dj6UcLMxWN9qMVlFxpLnfR-wLNXVhA0";
    }

    public static Stream<UUID> streamUUID(){
        return List.of(UUID.randomUUID(),UUID.randomUUID(),UUID.randomUUID()).stream();
    }


    public static Stream<String> streamValidRefreshToken(){
        return List.of("Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI0YmEwNzU5OC0xYzEyLTRlYTgtYTY2OS0wY2VlOWQ4M2I3YmEiLCJleHAiOjE2NTcxOTk2MDd9.8AYji3stOsSt5Uy2l8N4hYL_jwZquKwdSi0NCUJleVk",
                "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI0YmEwNzU5OC0xYzEyLTRlYTgtYTY2OS0wY2VlOWQ4M2I3YmEiLCJleHAiOjE2NTcyMDA0NTF9.-hEffRuv-FD5hChJ9XHIi8MY_PPwPM82JImO3rp89xQ",
                "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI0YmEwNzU5OC0xYzEyLTRlYTgtYTY2OS0wY2VlOWQ4M2I3YmEiLCJleHAiOjE2NTcyMDA0Njl9.GCtw60T52d21f_ff5zi6OcdT6LwPqYet1cuiuDfaIdM",
                "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI0YmEwNzU5OC0xYzEyLTRlYTgtYTY2OS0wY2VlOWQ4M2I3YmEiLCJleHAiOjE2NTcyMDA0ODl9.JKgXYi8ab8LzCdrLxu4bGkSQPgasgpOky3dzFh7nbAM").stream();
    }
    public static Stream<RequestUserDto> streamValidRequestUserDto(){
        return getListValidRequestUserDto().stream();
    }
    public static Stream<RequestUserDto> streamValidRequestUserDtoWithTypePhone_Number(){
        return getListValidRequestUserDtoWithTypePhone_Number().stream();
    }
    public static Stream<RequestUserDto> streamValidRequestUserDtoWithTypePassport(){
        return getListValidRequestUserDtoWithTypePassport().stream();
    }
    public static Stream<RequestUserDto> streamValidRequestUserDtoWithNotValidType(){
        return getListValidRequestUserDtoWithNotValidType().stream();
    }
    public static Stream<Arguments> streamArgumentsWithValidPasswords(){
        return getListArgumentsWitnValidPasswords().stream();
    }
    public static Stream<Arguments> streamArgumentsWithNotValidPasswords(){
        return getListArgumentsWitnNotValidPasswords().stream();
    }

    public static Stream<Arguments> streamArgumentsWithValidRequestAndResponsePasswords(){
        return getListArgumentsWitnValidRequestAndResponsePasswords().stream();
    }
    public static Stream<Arguments> streamArgumentsWithRefreshTokenAndExchange(){
        return getListArgumentsWithRefreshTokenAndExchange().stream();
    }

    private static List<Arguments> getListArgumentsWithRefreshTokenAndExchange(){
        String refresh1 = "Bearer asda32423$#43!!17nn!!1j!uu1(!J!L";
        MockServerWebExchange exchange1 = MockServerWebExchange
                .builder(MockServerHttpRequest.method(HttpMethod.GET, "/api/v1/login/token")
                        .header(HttpHeaders.AUTHORIZATION, refresh1))
                .build();
        String refresh2 = "Bearer asda32423$#43!!17nn!!1jsdf43343!uu1(!J!L";
        MockServerWebExchange exchange2 = MockServerWebExchange
                .builder(MockServerHttpRequest.method(HttpMethod.GET, "/api/v1/login/token")
                        .header(HttpHeaders.AUTHORIZATION, refresh1))
                .build();
        String refresh3 = "Bearer asda32423$#43!!23fdsf!!1jsdf43343!uu1(!J!L";
        MockServerWebExchange exchange3 = MockServerWebExchange
                .builder(MockServerHttpRequest.method(HttpMethod.GET, "/api/v1/login/token")
                        .header(HttpHeaders.AUTHORIZATION, refresh1))
                .build();
        return List.of(
                Arguments.of(refresh1, exchange1),
                Arguments.of(refresh2, exchange2),
                Arguments.of(refresh3, exchange3));
    }



    private static List<Arguments> getListArgumentsWitnValidRequestAndResponsePasswords(){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        RequestUserDto dto1 = new RequestUserDto("123456789012","qwe123WWW");
        RequestUserDto dto2 = new RequestUserDto("098765432109","123sdfDEfs");
        RequestUserDto dto3 = new RequestUserDto("123342545633","!ER@45667");
        ResponseLoginDto loginDto1 = new ResponseLoginDto(UUID.randomUUID(), passwordEncoder.encode("qwe123WWW"));
        ResponseLoginDto loginDto2 = new ResponseLoginDto(UUID.randomUUID(), passwordEncoder.encode("123sdfDEfs"));
        ResponseLoginDto loginDto3 = new ResponseLoginDto(UUID.randomUUID(), passwordEncoder.encode("!ER@45667"));
        return List.of(
                Arguments.of(dto1,loginDto1),
                Arguments.of(dto2,loginDto2),
                Arguments.of(dto3,loginDto3));
    }


    private static List<Arguments> getListArgumentsWitnNotValidPasswords(){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        UUID randomUUID1 = UUID.randomUUID();
        String requestPassword1 = "2!Cn444G56@";
        String responsePassword1 = passwordEncoder.encode("2!33dd@");
        UUID randomUUID2 = UUID.randomUUID();
        String requestPassword2 = "23!rrGHYerr3";
        String responsePassword2 = passwordEncoder.encode("23!rrGasdasd");
        UUID randomUUID3 = UUID.randomUUID();
        String requestPassword3 = "asD$loh$123";
        String responsePassword3 = passwordEncoder.encode("asD$loh$qwdd");;
        return List.of(
                Arguments.of(randomUUID1,requestPassword1,responsePassword1),
                Arguments.of(randomUUID2,requestPassword2,responsePassword2),
                Arguments.of(randomUUID3,requestPassword3,responsePassword3));
    }

    private static List<Arguments> getListArgumentsWitnValidPasswords(){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        UUID randomUUID1 = UUID.randomUUID();
        String requestPassword1 = "2!Cn444G56";
        String responsePassword1 = passwordEncoder.encode("2!Cn444G56");
        UUID randomUUID2 = UUID.randomUUID();
        String requestPassword2 = "23!rrGHYerr3";
        String responsePassword2 = passwordEncoder.encode("23!rrGHYerr3");
        UUID randomUUID3 = UUID.randomUUID();
        String requestPassword3 = "asD$loh$123";
        String responsePassword3 = passwordEncoder.encode("asD$loh$123");
        return List.of(
                Arguments.of(randomUUID1,requestPassword1,responsePassword1),
                Arguments.of(randomUUID2,requestPassword2,responsePassword2),
                Arguments.of(randomUUID3,requestPassword3,responsePassword3));
    }


    private static List getListValidRequestUserDtoWithNotValidType(){
        RequestUserDto dto3 = new RequestUserDto("1122 333444","VVssss2212");
        RequestUserDto dto4 = new RequestUserDto("1122 333444","VVssss2212");
        RequestUserDto dto5 = new RequestUserDto("1234567890","$#qweq123QQQ");
        RequestUserDto dto6 = new RequestUserDto("1234 123456","$#qweq123QQQ");
        return List.of(dto3,dto4,dto5,dto6);
    }

    private static List getListValidRequestUserDtoWithTypePassport(){
        RequestUserDto dto4 = new RequestUserDto("1122 333444","VVssss2212");
        RequestUserDto dto5 = new RequestUserDto("1234567890","$#qweq123QQQ");
        RequestUserDto dto6 = new RequestUserDto("1234 123456","$#qweq123QQQ");
        return List.of(dto4,dto5,dto6);
    }
    private static List getListValidRequestUserDtoWithTypePhone_Number(){
        RequestUserDto dto1 = new RequestUserDto("123456789012","qwe123WWW");
        RequestUserDto dto2 = new RequestUserDto("098765432109","123$!!asd");
        RequestUserDto dto3 = new RequestUserDto("123342545633","qwee!!$$VV");
        return List.of(dto1,dto2,dto3);
    }



    public static Stream<RequestUserDto> streamRequestUserDtoWithNotValidLogin(){
        return getListRequestUserDtoWithNotValidLogin().stream();
    }
    public static Stream<RequestUserDto> streamRequestUserDtoWithNotValidPassword(){
        return getListRequestUserDtoWithNotValidPassword().stream();
    }
    public static Stream<RequestUserDto> streamRequestUserDtoWithNotValidType(){
        return getListRequestUserDtoWithNotValidType().stream();
    }

    private static List getListRequestUserDtoWithNotValidType(){
        RequestUserDto dto1 = new RequestUserDto("123456789012","qwe123WWW");
        RequestUserDto dto2 = new RequestUserDto("098765432109","123$!!asd");
        RequestUserDto dto3 = new RequestUserDto("123342545633","qwee!!$$VV");
        RequestUserDto dto4 = new RequestUserDto("1122 333444","VVssss2212");
        RequestUserDto dto5 = new RequestUserDto("1234567890","$#qweq123QQQ");
        RequestUserDto dto6 = new RequestUserDto("1234 123456","$#qweq123QQQ");
        return List.of(dto1,dto2,dto3,dto4,dto5,dto6);
    }

    private static List getListRequestUserDtoWithNotValidPassword(){
        RequestUserDto dto1 = new RequestUserDto("123456789012","qwe123324234");
        RequestUserDto dto2 = new RequestUserDto("098765432109","123sdfsdfs");
        RequestUserDto dto3 = new RequestUserDto("123342545633","@!!$WWWWW");
        RequestUserDto dto4 = new RequestUserDto("1122 333444","WWWFSDFSDF");
        RequestUserDto dto5 = new RequestUserDto("1234567890","sdfsdfsdfsdf");
        RequestUserDto dto6 = new RequestUserDto("1234 123456","234234234234");
        return List.of(dto1,dto2,dto3,dto4,dto5,dto6);
    }
    private static List getListRequestUserDtoWithNotValidLogin(){
        RequestUserDto dto1 = new RequestUserDto("123456","qwe123WWW");
        RequestUserDto dto2 = new RequestUserDto("098765432109345","123$!!asd");
        RequestUserDto dto3 = new RequestUserDto("adasd asdas","qwee!!$$VV");
        RequestUserDto dto4 = new RequestUserDto("asdasd213123","VVssss2212");
        RequestUserDto dto5 = new RequestUserDto("!!!2333ffff","$#qweq123QQQ");
        RequestUserDto dto6 = new RequestUserDto("11111112ff44","$#qweq123QQQ");
        return List.of(dto1,dto2,dto3,dto4,dto5,dto6);
    }

    private static List getListValidRequestUserDto(){
        RequestUserDto dto1 = new RequestUserDto("443456789012","qwe123WWW");
        RequestUserDto dto2 = new RequestUserDto("448765432109","123$!!asd");
        RequestUserDto dto3 = new RequestUserDto("443342545633","qwee!!$$VV");
        RequestUserDto dto4 = new RequestUserDto("GBR123456789","VVssss2212");
        RequestUserDto dto5 = new RequestUserDto("GBS123456789","$#qweq123QQQ");
        RequestUserDto dto6 = new RequestUserDto("GBN 123456789","$#qweq123QQQ");
        return List.of(dto1,dto2,dto3,dto4,dto5,dto6);
    }

    public static TokenDto getTokenDto(){
        TokenDto tokenDto = new TokenDto("Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI0YmEwNzU5OC0xYzEyLTRlYTgtYTY2OS0wY2VlOWQ4M2I3YmEiLCJleHAiOjE2NTcxOTk2MDd9.8AYji3stOsSt5Uy2l8N4hYL_jwZquKwdSi0NCUJleVk",
                "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI0YmEwNzU5OC0xYzEyLTRlYTgtYTY2OS0wY2VlOWQ4M2I3YmEiLCJleHAiOjE2NTcxOTk2MDd9.8AYji3stOsSt5Uy2l8N4hYL_jwZquKwdSi0NCUJleVk");
        return tokenDto;
    }

    public static ResponseLoginDto getResponseLoginDto(){
        ResponseLoginDto responseLoginDto = new ResponseLoginDto(UUID.randomUUID(),"asde213!WW");
        return responseLoginDto;
    }
    public static UUID getUserId(){
        return UUID.randomUUID();
    }
    public static String getNotValidToken(){
        return "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIwMTY5MWYzMS05M2U3LTQ2MzktYjQ0Ni04YjU2NTIzMzk2YzkiLCJleHAiOjE2NTk0MzA2ODB9.-O51Q-Yk-izzFXvM6yY9eiCOsjr6jnroj3M6az5Cx2I";
    }

}

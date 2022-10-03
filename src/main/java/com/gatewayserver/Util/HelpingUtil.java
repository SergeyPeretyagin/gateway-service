package com.gatewayserver.Util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.gatewayserver.security.JwtProvider.BEARER;

@Slf4j
public class HelpingUtil {
    private static final String GET_ID_BY_PHONE_NUMBER_OR_PASSPORT = "/login";
    public static boolean checkPassword (String password1, String password2){
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        log.info("checkPassword method {}, {} ", password1,password2);
        return passwordEncoder.matches(password1,password2);
    }

    public static boolean checkIdFromToken(String uuid1, String uuid2){
        log.info("HelpingUtil.{} checkIdFromToken method {}", uuid1, uuid2);
        return uuid1.equals(uuid2);
    }

    public static boolean checkTokenContainsBearer (String bearerToken){
        log.info("HelpingUtil. ResolveToken method {}", bearerToken);
        if (bearerToken != null && bearerToken.startsWith(BEARER)) {
            return true;
        }
        log.info("HelpingUtil. ResolveToken method. No found 'Bearer'");
        return false;
    }
    public static String  getEndPointToServiceUser() {
        return GET_ID_BY_PHONE_NUMBER_OR_PASSPORT;
    }
}

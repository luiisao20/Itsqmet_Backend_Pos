package com.lbravo.utils;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;

@Component
public class CookiesUtils {

    private static String sameSite;
    private static boolean secure;

    public CookiesUtils(
            @Value("${app.cookie.same-site:Lax}") String sameSite,
            @Value("${app.cookie.secure:false}") boolean secure) {
        CookiesUtils.sameSite = sameSite;
        CookiesUtils.secure = secure;
    }

    public static String extractTokenFromCookies(Cookie[] cookies) {
        if (cookies == null) {
            return null;
        }
        return Arrays.stream(cookies)
                .filter(c -> "jwt".equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    public static ResponseCookie createJwtCookie(String jwt, long maxAge) {
        return ResponseCookie.from("jwt", jwt)
                .httpOnly(true)
                .secure(secure)
                .path("/")
                .maxAge(maxAge)
                .sameSite(sameSite)
                .build();
    }

    public static ResponseCookie deleteJwtCookie() {
        return ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(secure)
                .path("/")
                .maxAge(0)
                .sameSite(sameSite)
                .build();
    }
}

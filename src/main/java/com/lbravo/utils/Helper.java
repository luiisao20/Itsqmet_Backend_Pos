package com.lbravo.utils;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletResponse;

public class Helper {

    public static ResponseEntity<?> getResponseMessage(String msg, HttpStatus status) {
        return ResponseEntity.status(status).body(Map.of("message", msg));
    }

    public static void addJwtCookie(HttpServletResponse res, String jwt, long maxAge) {
        res.addHeader(HttpHeaders.SET_COOKIE, CookiesUtils.createJwtCookie(jwt, maxAge).toString());
    }

    public static void deleteJwtCookie(HttpServletResponse res) {
        res.addHeader(HttpHeaders.SET_COOKIE, CookiesUtils.deleteJwtCookie().toString());
    }
}

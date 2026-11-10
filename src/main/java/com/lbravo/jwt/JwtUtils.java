package com.lbravo.jwt;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {

  public static Claims extractClaims(String token) {
    SecretKey key = Keys.hmacShaKeyFor(JwtConstants.getSecretKey().getBytes(StandardCharsets.UTF_8));
    return Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  public static String extractEmail(String token) {
    return extractClaims(token).get("email", String.class);
  }

  public static String extractUuid(String token) {
    return extractClaims(token).get("uuid", String.class);
  }

  public static String extractRol(String token) {
    return extractClaims(token).get("rol", String.class);
  }
}

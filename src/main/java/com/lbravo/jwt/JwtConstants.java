package com.lbravo.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class JwtConstants {

  public static final String JWT_HEADER = "Authorization";
  public static final String TOKEN_PREFIX = "Bearer ";

  public static String SECRET_KEY_STATIC;

  @Value("${spring.jwt.secret:restaurantSecretKeyForPOSSystemMustBeAtLeast64CharactersLong1234567890abcdef}")
  private String secretKey;

  @PostConstruct
  public void init() {
    SECRET_KEY_STATIC = secretKey;
  }

  public static String getSecretKey() {
    return SECRET_KEY_STATIC;
  }
}

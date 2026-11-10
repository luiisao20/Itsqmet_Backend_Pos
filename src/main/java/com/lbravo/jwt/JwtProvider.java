package com.lbravo.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.crypto.SecretKey;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JwtProvider {

  private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000L; // 24 horas

  public static String generateToken(Authentication auth, String userUuid, String rol) {
    Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
    String roles = populateAuthorities(authorities);

    SecretKey key = Keys.hmacShaKeyFor(JwtConstants.getSecretKey().getBytes(StandardCharsets.UTF_8));

    return Jwts.builder()
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
        .claim("email", auth.getName())
        .claim("uuid", userUuid)
        .claim("rol", rol)
        .claim("authorities", roles)
        .signWith(key)
        .compact();
  }

  private static String populateAuthorities(Collection<? extends GrantedAuthority> authorities) {
    Set<String> auths = new HashSet<>();
    for (GrantedAuthority grantedAuthority : authorities) {
      auths.add(grantedAuthority.getAuthority());
    }
    return String.join(",", auths);
  }
}

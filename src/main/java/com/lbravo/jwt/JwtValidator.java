package com.lbravo.jwt;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.lbravo.utils.CookiesUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtValidator extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String requestPath = request.getServletPath();

    // Rutas públicas que no requieren validación de JWT
    if ("/auth/login".equals(requestPath)
        || "/auth/csrf".equals(requestPath)
        || requestPath.startsWith("/v3/api-docs")
        || requestPath.startsWith("/swagger-ui")
        || requestPath.startsWith("/ws-pos")
        || requestPath.startsWith("/dev")) {
      filterChain.doFilter(request, response);
      return;
    }

    String jwt = CookiesUtils.extractTokenFromCookies(request.getCookies());

    // Fallback al header Authorization si no está en cookies
    if (jwt == null) {
      String header = request.getHeader(JwtConstants.JWT_HEADER);
      if (header != null && header.startsWith(JwtConstants.TOKEN_PREFIX)) {
        jwt = header.substring(JwtConstants.TOKEN_PREFIX.length());
      }
    }

    if (jwt != null) {
      try {
        SecretKey key = Keys.hmacShaKeyFor(JwtConstants.getSecretKey().getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(jwt)
            .getBody();

        String email = String.valueOf(claims.get("email"));
        String authorities = String.valueOf(claims.get("authorities"));
        List<GrantedAuthority> auth = AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);

        Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, auth);
        SecurityContextHolder.getContext().setAuthentication(authentication);

      } catch (Exception e) {
        log.warn("Error de autenticación JWT: {}", e.getMessage());
        if ("/auth/logout".equals(requestPath)) {
          filterChain.doFilter(request, response);
          return;
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"error\": \"No autorizado\", \"message\": \"Sesión inválida o expirada\"}");
        return;
      }
    }

    filterChain.doFilter(request, response);
  }
}

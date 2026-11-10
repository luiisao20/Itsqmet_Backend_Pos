package com.lbravo.configuration;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lbravo.jwt.JwtValidator;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class GlobalConfig {

  private final JwtValidator jwtValidator;

  @Value("${app.cookie.same-site:Lax}")
  private String cookieSameSite;

  @Value("${app.cookie.secure:false}")
  private boolean cookieSecure;

  @Value("${app.cors.allowed-origins:http://localhost:3000,http://localhost:5173,http://localhost:4200}")
  private String[] allowedOrigins;

  public GlobalConfig(JwtValidator jwtValidator) {
    this.jwtValidator = jwtValidator;
  }

  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .exceptionHandling(handling -> handling
            .authenticationEntryPoint((request, response, authException) -> {
              response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
              response.setContentType("application/json");
              response.getWriter()
                  .write("{\"error\": \"No autorizado\", \"message\": \"Sesión inválida o inexistente\"}");
            })
            .accessDeniedHandler((request, response, accessDeniedException) -> {
              response.setStatus(HttpServletResponse.SC_FORBIDDEN);
              response.setContentType("application/json");
              response.getWriter()
                  .write("{\"error\": \"Acceso denegado\", \"message\": \"No tienes permisos para este recurso\"}");
            }))
        .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(csrf -> csrf.disable())
        .httpBasic(Customizer.withDefaults())
        .formLogin(form -> form.disable())
        .authorizeHttpRequests(auth -> auth
            .dispatcherTypeMatchers(DispatcherType.ASYNC, DispatcherType.ERROR).permitAll()
            .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
            .requestMatchers("/auth/login", "/auth/csrf").permitAll()
            .requestMatchers("/ws-pos/**", "/dev/**").permitAll()
            .requestMatchers("/auth/logout", "/auth/me").authenticated()

            // Gestión Administrativa (Solo ADMIN)
            // .requestMatchers("/usuarios/**", "/distribuidores/**",
            // "/compras/**").hasRole("ADMIN")

            // Operaciones de Salón y Atención (ADMIN y MESERO)
            .requestMatchers("/mesas/**", "/pedidos/**", "/clientes/**", "/facturas/**", "/pagos/**")
            .hasAnyRole("ADMIN", "MESERO")

            // Cocina y Preparación de platos (ADMIN, COCINA, MESERO)
            .requestMatchers("/detalles-pedido/**").hasAnyRole("ADMIN", "COCINA", "MESERO")

            // Catálogo e Insumos (Lectura para todos los roles; Modificación solo ADMIN)
            .requestMatchers(HttpMethod.GET, "/platos/**", "/categorias-plato/**", "/insumos/**", "/recetas/**")
            .hasAnyRole("ADMIN", "MESERO", "COCINA")
            .requestMatchers(HttpMethod.POST, "/platos/**", "/categorias-plato/**", "/insumos/**", "/recetas/**")
            .hasRole("ADMIN")
            .requestMatchers(HttpMethod.PUT, "/platos/**", "/categorias-plato/**", "/insumos/**", "/recetas/**")
            .hasRole("ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/platos/**", "/categorias-plato/**", "/insumos/**", "/recetas/**")
            .hasRole("ADMIN")

            .anyRequest().permitAll())
        .addFilterBefore(jwtValidator, BasicAuthenticationFilter.class);

    return http.build();
  }

  private CookieCsrfTokenRepository csrfTokenRepository() {
    CookieCsrfTokenRepository repository = CookieCsrfTokenRepository.withHttpOnlyFalse();
    repository.setCookieCustomizer(cookie -> cookie
        .sameSite(cookieSameSite)
        .secure(cookieSecure));
    return repository;
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOriginPatterns(Arrays.stream(allowedOrigins).map(String::trim).toList());
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
    config.setAllowedHeaders(List.of(
        "Authorization",
        "Content-Type",
        "X-XSRF-TOKEN",
        "X-Requested-With"));
    config.setExposedHeaders(List.of("Set-Cookie", "New-Token"));
    config.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }

  @Bean
  @Primary
  public ObjectMapper objectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    return mapper;
  }
}

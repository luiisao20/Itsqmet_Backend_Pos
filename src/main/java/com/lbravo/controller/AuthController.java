package com.lbravo.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lbravo.dto.AuthDTO;
import com.lbravo.service.AuthService;
import com.lbravo.service.AuthService.LoginResult;
import com.lbravo.utils.Helper;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

  private final AuthService authService;

  @GetMapping("/csrf")
  public ResponseEntity<Map<String, String>> csrf(CsrfToken token) {
    return ResponseEntity.ok(Map.of("token", token != null ? token.getToken() : ""));
  }

  @PostMapping("/login")
  public ResponseEntity<AuthDTO.AuthResponse> login(
      @Valid @RequestBody AuthDTO.LoginRequest req,
      HttpServletResponse response) {
    LoginResult result = authService.login(req);
    // Cookie con HttpOnly, 24 horas de vigencia (86400 segundos)
    Helper.addJwtCookie(response, result.getJwtToken(), 86400);
    return ResponseEntity.ok(result.getAuthResponse());
  }

  @PostMapping("/cliente/login")
  public ResponseEntity<AuthDTO.AuthResponse> loginCliente(
      @Valid @RequestBody AuthDTO.ClienteLoginRequest req,
      HttpServletResponse response) {
    LoginResult result = authService.loginCliente(req);
    Helper.addJwtCookie(response, result.getJwtToken(), 86400);
    return ResponseEntity.ok(result.getAuthResponse());
  }

  @PostMapping("/cliente/registro")
  public ResponseEntity<AuthDTO.AuthResponse> registerCliente(
      @Valid @RequestBody AuthDTO.ClienteRegisterRequest req,
      HttpServletResponse response) {
    LoginResult result = authService.registerCliente(req);
    Helper.addJwtCookie(response, result.getJwtToken(), 86400);
    return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(result.getAuthResponse());
  }

  @PostMapping("/logout")
  public ResponseEntity<?> logout(HttpServletResponse response) {
    Helper.deleteJwtCookie(response);
    return Helper.getResponseMessage("Sesión cerrada correctamente", org.springframework.http.HttpStatus.OK);
  }

  @GetMapping("/me")
  public ResponseEntity<AuthDTO.AuthResponse> getCurrentUser(Authentication auth) {
    return ResponseEntity.ok(authService.getCurrentUserInfo(auth.getName()));
  }
}

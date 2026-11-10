package com.lbravo.service;

import java.util.List;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.lbravo.dto.AuthDTO;
import com.lbravo.entity.Usuario;
import com.lbravo.exception.BadRequestException;
import com.lbravo.exception.ResourceNotFoundException;
import com.lbravo.jwt.JwtProvider;
import com.lbravo.repository.UsuarioRepository;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Service
@AllArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Data
    @AllArgsConstructor
    @Builder
    public static class LoginResult {
        private AuthDTO.AuthResponse authResponse;
        private String jwtToken;
    }

    public LoginResult login(AuthDTO.LoginRequest req) {
        Usuario usuario = usuarioRepository.findByEmail(req.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));

        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new BadRequestException("El usuario se encuentra inactivo");
        }

        if (!passwordEncoder.matches(req.getPassword(), usuario.getPassword())) {
            throw new BadCredentialsException("Credenciales inválidas");
        }

        String roleName = "ROLE_" + usuario.getRol().name();
        Authentication auth = new UsernamePasswordAuthenticationToken(
                usuario.getEmail(),
                null,
                List.of(new SimpleGrantedAuthority(roleName)));

        String jwt = JwtProvider.generateToken(auth, usuario.getUuid(), usuario.getRol().name());

        AuthDTO.AuthResponse response = AuthDTO.AuthResponse.builder()
                .uuid(usuario.getUuid())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .email(usuario.getEmail())
                .rol(usuario.getRol())
                .activo(usuario.getActivo())
                .message("Autenticación exitosa")
                .build();

        return LoginResult.builder()
                .authResponse(response)
                .jwtToken(jwt)
                .build();
    }

    public AuthDTO.AuthResponse getCurrentUserInfo(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + email));

        return AuthDTO.AuthResponse.builder()
                .uuid(usuario.getUuid())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .email(usuario.getEmail())
                .rol(usuario.getRol())
                .activo(usuario.getActivo())
                .message("Sesión válida")
                .build();
    }
}

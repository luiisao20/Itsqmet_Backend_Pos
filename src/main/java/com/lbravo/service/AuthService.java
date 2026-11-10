package com.lbravo.service;

import java.util.List;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lbravo.dto.AuthDTO;
import com.lbravo.entity.Cliente;
import com.lbravo.entity.Usuario;
import com.lbravo.exception.BadRequestException;
import com.lbravo.exception.ResourceNotFoundException;
import com.lbravo.jwt.JwtProvider;
import com.lbravo.repository.ClienteRepository;
import com.lbravo.repository.UsuarioRepository;
import com.lbravo.types.RolUsuario;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Service
@AllArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
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

    public LoginResult loginCliente(AuthDTO.ClienteLoginRequest req) {
        String login = req.getLogin().trim();
        Cliente cliente = clienteRepository.findByEmailOrIdentificacion(login)
                .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));

        if (!Boolean.TRUE.equals(cliente.getActivo())) {
            throw new BadRequestException("La cuenta del cliente se encuentra inactiva");
        }

        if (cliente.getPassword() == null || !passwordEncoder.matches(req.getPassword(), cliente.getPassword())) {
            throw new BadCredentialsException("Credenciales inválidas");
        }

        String roleName = "ROLE_" + RolUsuario.CLIENTE.name();
        Authentication auth = new UsernamePasswordAuthenticationToken(
                cliente.getEmail() != null ? cliente.getEmail() : cliente.getIdentificacion(),
                null,
                List.of(new SimpleGrantedAuthority(roleName)));

        String jwt = JwtProvider.generateToken(auth, cliente.getUuid(), RolUsuario.CLIENTE.name());

        AuthDTO.AuthResponse response = AuthDTO.AuthResponse.builder()
                .uuid(cliente.getUuid())
                .nombre(cliente.getRazonSocial())
                .apellido("")
                .email(cliente.getEmail())
                .rol(RolUsuario.CLIENTE)
                .activo(cliente.getActivo())
                .message("Autenticación de cliente exitosa")
                .build();

        return LoginResult.builder()
                .authResponse(response)
                .jwtToken(jwt)
                .build();
    }

    @Transactional
    public LoginResult registerCliente(AuthDTO.ClienteRegisterRequest req) {
        String iden = req.getIdentificacion().trim();
        if (clienteRepository.existsByIdentificacion(iden)) {
            throw new BadRequestException("Ya existe un cliente con la identificación: " + iden);
        }

        String email = req.getEmail().trim().toLowerCase();
        if (clienteRepository.existsByEmail(email)) {
            throw new BadRequestException("Ya existe un cliente con el email: " + email);
        }

        Cliente cliente = Cliente.builder()
                .tipoIdentificacion(req.getTipoIdentificacion())
                .identificacion(iden)
                .razonSocial(req.getRazonSocial().trim())
                .direccion(req.getDireccion() != null ? req.getDireccion().trim() : null)
                .telefono(req.getTelefono() != null ? req.getTelefono().trim() : null)
                .email(email)
                .password(passwordEncoder.encode(req.getPassword()))
                .activo(true)
                .build();

        Cliente saved = clienteRepository.save(cliente);

        String roleName = "ROLE_" + RolUsuario.CLIENTE.name();
        Authentication auth = new UsernamePasswordAuthenticationToken(
                saved.getEmail(),
                null,
                List.of(new SimpleGrantedAuthority(roleName)));

        String jwt = JwtProvider.generateToken(auth, saved.getUuid(), RolUsuario.CLIENTE.name());

        AuthDTO.AuthResponse response = AuthDTO.AuthResponse.builder()
                .uuid(saved.getUuid())
                .nombre(saved.getRazonSocial())
                .apellido("")
                .email(saved.getEmail())
                .rol(RolUsuario.CLIENTE)
                .activo(saved.getActivo())
                .message("Registro de cliente exitoso")
                .build();

        return LoginResult.builder()
                .authResponse(response)
                .jwtToken(jwt)
                .build();
    }

    public AuthDTO.AuthResponse getCurrentUserInfo(String emailOrLogin) {
        // Intentar buscar como Usuario interno
        Usuario usuario = usuarioRepository.findByEmail(emailOrLogin).orElse(null);
        if (usuario != null) {
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

        // Si no es usuario, buscar como Cliente
        Cliente cliente = clienteRepository.findByEmailOrIdentificacion(emailOrLogin)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario o cliente no encontrado con: " + emailOrLogin));

        return AuthDTO.AuthResponse.builder()
                .uuid(cliente.getUuid())
                .nombre(cliente.getRazonSocial())
                .apellido("")
                .email(cliente.getEmail())
                .rol(RolUsuario.CLIENTE)
                .activo(cliente.getActivo())
                .message("Sesión válida de cliente")
                .build();
    }
}

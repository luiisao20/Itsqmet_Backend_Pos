package com.lbravo.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lbravo.dto.UsuarioDTO;
import com.lbravo.entity.Usuario;
import com.lbravo.exception.BadRequestException;
import com.lbravo.exception.ResourceNotFoundException;
import com.lbravo.repository.UsuarioRepository;
import com.lbravo.types.RolUsuario;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UsuarioDTO.Response mapToDto(Usuario u) {
        return UsuarioDTO.Response.builder()
                .uuid(u.getUuid())
                .nombre(u.getNombre())
                .apellido(u.getApellido())
                .cedula(u.getCedula())
                .email(u.getEmail())
                .rol(u.getRol())
                .activo(u.getActivo())
                .createdAt(u.getCreatedAt())
                .build();
    }

    public Page<UsuarioDTO.Response> getAll(Pageable pageable) {
        return usuarioRepository.findAll(pageable).map(this::mapToDto);
    }

    public UsuarioDTO.Response getById(String uuid) {
        Usuario u = usuarioRepository.findById(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con UUID: " + uuid));
        return mapToDto(u);
    }

    public Page<UsuarioDTO.Response> getByRol(RolUsuario rol, Pageable pageable) {
        return usuarioRepository.findByRol(rol, pageable).map(this::mapToDto);
    }

    public Page<UsuarioDTO.Response> search(String query, Pageable pageable) {
        return usuarioRepository.searchUsuarios(query, pageable).map(this::mapToDto);
    }

    @Transactional
    public UsuarioDTO.Response create(UsuarioDTO.Request req) {
        if (usuarioRepository.existsByEmail(req.getEmail().toLowerCase().trim())) {
            throw new BadRequestException("Ya existe un usuario registrado con el email: " + req.getEmail());
        }
        if (usuarioRepository.existsByCedula(req.getCedula().trim())) {
            throw new BadRequestException("Ya existe un usuario registrado con la cédula: " + req.getCedula());
        }
        if (req.getPassword() == null || req.getPassword().isBlank()) {
            throw new BadRequestException("La contraseña es requerida para registrar un nuevo usuario");
        }

        Usuario usuario = Usuario.builder()
                .nombre(req.getNombre().trim())
                .apellido(req.getApellido().trim())
                .cedula(req.getCedula().trim())
                .email(req.getEmail().toLowerCase().trim())
                .password(passwordEncoder.encode(req.getPassword()))
                .rol(req.getRol() != null ? req.getRol() : RolUsuario.ADMIN)
                .activo(req.getActivo() != null ? req.getActivo() : true)
                .build();

        return mapToDto(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioDTO.Response update(String uuid, UsuarioDTO.Request req) {
        Usuario usuario = usuarioRepository.findById(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con UUID: " + uuid));

        String newEmail = req.getEmail().toLowerCase().trim();
        if (!usuario.getEmail().equalsIgnoreCase(newEmail) && usuarioRepository.existsByEmail(newEmail)) {
            throw new BadRequestException("Ya existe un usuario registrado con el email: " + req.getEmail());
        }

        String newCedula = req.getCedula().trim();
        if (!usuario.getCedula().equalsIgnoreCase(newCedula) && usuarioRepository.existsByCedula(newCedula)) {
            throw new BadRequestException("Ya existe un usuario registrado con la cédula: " + req.getCedula());
        }

        usuario.setNombre(req.getNombre().trim());
        usuario.setApellido(req.getApellido().trim());
        usuario.setCedula(newCedula);
        usuario.setEmail(newEmail);
        if (req.getRol() != null) {
            usuario.setRol(req.getRol());
        }
        if (req.getActivo() != null) {
            usuario.setActivo(req.getActivo());
        }
        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(req.getPassword()));
        }

        return mapToDto(usuarioRepository.save(usuario));
    }

    @Transactional
    public void delete(String uuid) {
        Usuario usuario = usuarioRepository.findById(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con UUID: " + uuid));
        usuarioRepository.delete(usuario);
    }
}

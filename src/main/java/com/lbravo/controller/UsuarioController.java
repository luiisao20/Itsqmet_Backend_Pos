package com.lbravo.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lbravo.dto.UsuarioDTO;
import com.lbravo.service.UsuarioService;
import com.lbravo.types.RolUsuario;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/usuarios")
@AllArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<Page<UsuarioDTO.Response>> getAll(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(usuarioService.getAll(pageable));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<UsuarioDTO.Response> getById(@PathVariable String uuid) {
        return ResponseEntity.ok(usuarioService.getById(uuid));
    }

    @GetMapping("/rol/{rol}")
    public ResponseEntity<Page<UsuarioDTO.Response>> getByRol(
            @PathVariable RolUsuario rol,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(usuarioService.getByRol(rol, pageable));
    }

    @GetMapping("/buscar")
    public ResponseEntity<Page<UsuarioDTO.Response>> search(
            @RequestParam(required = false) String query,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(usuarioService.search(query, pageable));
    }

    @PostMapping
    public ResponseEntity<UsuarioDTO.Response> create(@Valid @RequestBody UsuarioDTO.Request req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.create(req));
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<UsuarioDTO.Response> update(@PathVariable String uuid, @Valid @RequestBody UsuarioDTO.Request req) {
        return ResponseEntity.ok(usuarioService.update(uuid, req));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> delete(@PathVariable String uuid) {
        usuarioService.delete(uuid);
        return ResponseEntity.noContent().build();
    }
}

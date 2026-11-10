package com.lbravo.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lbravo.dto.MesaDTO;
import com.lbravo.service.MesaService;
import com.lbravo.types.EstadoMesa;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/mesas")
@AllArgsConstructor
public class MesaController {

    private final MesaService mesaService;

    @GetMapping
    public ResponseEntity<Page<MesaDTO.Response>> getAll(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(mesaService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MesaDTO.Response> getById(@PathVariable Long id) {
        return ResponseEntity.ok(mesaService.getById(id));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<Page<MesaDTO.Response>> getByEstado(
            @PathVariable EstadoMesa estado,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(mesaService.getByEstado(estado, pageable));
    }

    @GetMapping("/buscar")
    public ResponseEntity<Page<MesaDTO.Response>> searchByUbicacion(
            @RequestParam String ubicacion,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(mesaService.searchByUbicacion(ubicacion, pageable));
    }

    @PostMapping
    public ResponseEntity<MesaDTO.Response> create(@Valid @RequestBody MesaDTO.Request req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mesaService.create(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MesaDTO.Response> update(@PathVariable Long id, @Valid @RequestBody MesaDTO.Request req) {
        return ResponseEntity.ok(mesaService.update(id, req));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<MesaDTO.Response> updateEstado(@PathVariable Long id, @RequestParam EstadoMesa estado) {
        return ResponseEntity.ok(mesaService.updateEstado(id, estado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        mesaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

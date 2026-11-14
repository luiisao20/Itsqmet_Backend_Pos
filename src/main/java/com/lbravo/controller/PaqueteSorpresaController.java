package com.lbravo.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lbravo.dto.PaqueteSorpresaDTO;
import com.lbravo.service.PaqueteSorpresaService;
import com.lbravo.types.EstadoPaqueteSorpresa;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/paquetes-sorpresa")
@RequiredArgsConstructor
public class PaqueteSorpresaController {

    private final PaqueteSorpresaService paqueteSorpresaService;

    @GetMapping("/disponibles")
    public ResponseEntity<List<PaqueteSorpresaDTO.Response>> getDisponibles() {
        return ResponseEntity.ok(paqueteSorpresaService.getDisponiblesParaClientes());
    }

    @GetMapping("/gestion")
    public ResponseEntity<List<PaqueteSorpresaDTO.Response>> getGestion(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(paqueteSorpresaService.getGestionAdmin(fecha));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaqueteSorpresaDTO.Response> getById(@PathVariable Long id) {
        return ResponseEntity.ok(paqueteSorpresaService.getById(id));
    }

    @PostMapping
    public ResponseEntity<PaqueteSorpresaDTO.Response> create(@Valid @RequestBody PaqueteSorpresaDTO.Request req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paqueteSorpresaService.create(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaqueteSorpresaDTO.Response> update(
            @PathVariable Long id,
            @Valid @RequestBody PaqueteSorpresaDTO.Request req) {
        return ResponseEntity.ok(paqueteSorpresaService.update(id, req));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<PaqueteSorpresaDTO.Response> cambiarEstado(
            @PathVariable Long id,
            @RequestParam EstadoPaqueteSorpresa nuevoEstado) {
        return ResponseEntity.ok(paqueteSorpresaService.cambiarEstado(id, nuevoEstado));
    }

    @PostMapping("/{id}/rescatar")
    public ResponseEntity<PaqueteSorpresaDTO.RescateResponse> rescatar(
            @PathVariable Long id,
            @RequestBody(required = false) PaqueteSorpresaDTO.RescateRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paqueteSorpresaService.rescatarPaquete(id, req));
    }
}

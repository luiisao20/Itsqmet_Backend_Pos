package com.lbravo.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lbravo.dto.TurnoDTO;
import com.lbravo.service.TurnoService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/turnos")
@AllArgsConstructor
public class TurnoController {

  private final TurnoService turnoService;

  @GetMapping
  public ResponseEntity<Page<TurnoDTO.Response>> getAll(@PageableDefault(size = 50) Pageable pageable) {
    return ResponseEntity.ok(turnoService.getAll(pageable));
  }

  @GetMapping("/usuario/{uuid}")
  public ResponseEntity<Page<TurnoDTO.Response>> getByUsuario(
      @PathVariable String uuid,
      @PageableDefault(size = 50) Pageable pageable) {
    return ResponseEntity.ok(turnoService.getByUsuario(uuid, pageable));
  }

  @GetMapping("/activo/{uuid}")
  public ResponseEntity<TurnoDTO.Response> getTurnoActivo(@PathVariable String uuid) {
    return turnoService.getTurnoActivo(uuid)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.noContent().build());
  }

  @PostMapping("/iniciar")
  public ResponseEntity<TurnoDTO.Response> iniciarTurno(@Valid @RequestBody TurnoDTO.Request req) {
    return ResponseEntity.status(HttpStatus.CREATED).body(turnoService.iniciarTurno(req));
  }

  @PostMapping("/cerrar")
  public ResponseEntity<TurnoDTO.Response> cerrarTurno(@Valid @RequestBody TurnoDTO.Request req) {
    return ResponseEntity.ok(turnoService.cerrarTurno(req));
  }
}

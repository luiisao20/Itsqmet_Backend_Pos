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

import com.lbravo.dto.PlatoDTO;
import com.lbravo.service.PlatoService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/platos")
@AllArgsConstructor
public class PlatoController {

  private final PlatoService platoService;

  @GetMapping
  public ResponseEntity<Page<PlatoDTO.Response>> getAll(@PageableDefault(size = 20) Pageable pageable) {
    return ResponseEntity.ok(platoService.getAll(pageable));
  }

  @GetMapping("/{id}")
  public ResponseEntity<PlatoDTO.Response> getById(@PathVariable Long id) {
    return ResponseEntity.ok(platoService.getById(id));
  }

  @GetMapping("/categoria/{idCategoria}")
  public ResponseEntity<Page<PlatoDTO.Response>> getByCategoria(
      @PathVariable Long idCategoria,
      @PageableDefault(size = 20) Pageable pageable) {
    return ResponseEntity.ok(platoService.getByCategoria(idCategoria, pageable));
  }

  @GetMapping("/disponible/{disponible}")
  public ResponseEntity<Page<PlatoDTO.Response>> getByDisponible(
      @PathVariable Boolean disponible,
      @PageableDefault(size = 20) Pageable pageable) {
    return ResponseEntity.ok(platoService.getByDisponible(disponible, pageable));
  }

  @GetMapping("/buscar")
  public ResponseEntity<Page<PlatoDTO.Response>> search(
      @RequestParam(required = false) String nombre,
      @RequestParam(required = false) Long idCategoria,
      @RequestParam(required = false) Boolean disponible,
      @PageableDefault(size = 20) Pageable pageable) {
    return ResponseEntity.ok(platoService.search(nombre, idCategoria, disponible, pageable));
  }

  @PostMapping
  public ResponseEntity<PlatoDTO.Response> create(@Valid @RequestBody PlatoDTO.Request req) {
    return ResponseEntity.status(HttpStatus.CREATED).body(platoService.create(req));
  }

  @PutMapping("/{id}")
  public ResponseEntity<PlatoDTO.Response> update(@PathVariable Long id, @Valid @RequestBody PlatoDTO.Request req) {
    return ResponseEntity.ok(platoService.update(id, req));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    platoService.delete(id);
    return ResponseEntity.noContent().build();
  }
}

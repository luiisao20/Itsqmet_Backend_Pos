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

import com.lbravo.dto.ClienteDTO;
import com.lbravo.service.ClienteService;
import com.lbravo.types.TipoIdentificacion;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/clientes")
@AllArgsConstructor
public class ClienteController {

  private final ClienteService clienteService;

  @GetMapping
  public ResponseEntity<Page<ClienteDTO.Response>> getAll(@PageableDefault(size = 20) Pageable pageable) {
    return ResponseEntity.ok(clienteService.getAll(pageable));
  }

  @GetMapping("/{uuid}")
  public ResponseEntity<ClienteDTO.Response> getById(@PathVariable String uuid) {
    return ResponseEntity.ok(clienteService.getById(uuid));
  }

  @GetMapping("/identificacion/{identificacion}")
  public ResponseEntity<ClienteDTO.Response> getByIdentificacion(@PathVariable String identificacion) {
    return ResponseEntity.ok(clienteService.getByIdentificacion(identificacion));
  }

  @GetMapping("/tipo/{tipo}")
  public ResponseEntity<Page<ClienteDTO.Response>> getByTipo(
      @PathVariable TipoIdentificacion tipo,
      @PageableDefault(size = 20) Pageable pageable) {
    return ResponseEntity.ok(clienteService.getByTipo(tipo, pageable));
  }

  @GetMapping("/buscar")
  public ResponseEntity<Page<ClienteDTO.Response>> search(
      @RequestParam(required = false) String filtro,
      @PageableDefault(size = 20) Pageable pageable) {
    return ResponseEntity.ok(clienteService.search(filtro, pageable));
  }

  @PostMapping
  public ResponseEntity<ClienteDTO.Response> create(@Valid @RequestBody ClienteDTO.Request req) {
    return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.create(req));
  }

  @PutMapping("/{uuid}")
  public ResponseEntity<ClienteDTO.Response> update(@PathVariable String uuid,
      @Valid @RequestBody ClienteDTO.Request req) {
    return ResponseEntity.ok(clienteService.update(uuid, req));
  }

  @DeleteMapping("/{uuid}")
  public ResponseEntity<Void> delete(@PathVariable String uuid) {
    clienteService.delete(uuid);
    return ResponseEntity.noContent().build();
  }
}

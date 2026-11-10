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
import org.springframework.web.bind.annotation.RestController;

import com.lbravo.dto.RecetaDTO;
import com.lbravo.service.RecetaService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/recetas")
@AllArgsConstructor
public class RecetaController {

    private final RecetaService recetaService;

    @GetMapping
    public ResponseEntity<Page<RecetaDTO.Response>> getAll(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(recetaService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecetaDTO.Response> getById(@PathVariable Long id) {
        return ResponseEntity.ok(recetaService.getById(id));
    }

    @GetMapping("/plato/{idPlato}")
    public ResponseEntity<Page<RecetaDTO.Response>> getByPlato(
            @PathVariable Long idPlato,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(recetaService.getByPlato(idPlato, pageable));
    }

    @GetMapping("/insumo/{idInsumo}")
    public ResponseEntity<Page<RecetaDTO.Response>> getByInsumo(
            @PathVariable Long idInsumo,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(recetaService.getByInsumo(idInsumo, pageable));
    }

    @PostMapping
    public ResponseEntity<RecetaDTO.Response> create(@Valid @RequestBody RecetaDTO.Request req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(recetaService.create(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecetaDTO.Response> update(@PathVariable Long id, @Valid @RequestBody RecetaDTO.Request req) {
        return ResponseEntity.ok(recetaService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        recetaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

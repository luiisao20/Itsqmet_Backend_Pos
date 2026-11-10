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

import com.lbravo.dto.InsumoDTO;
import com.lbravo.service.InsumoService;
import com.lbravo.types.UnidadMedida;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/insumos")
@AllArgsConstructor
public class InsumoController {

    private final InsumoService insumoService;

    @GetMapping
    public ResponseEntity<Page<InsumoDTO.Response>> getAll(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(insumoService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InsumoDTO.Response> getById(@PathVariable Long id) {
        return ResponseEntity.ok(insumoService.getById(id));
    }

    @GetMapping("/stock-bajo")
    public ResponseEntity<Page<InsumoDTO.Response>> getStockBajo(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(insumoService.getStockBajo(pageable));
    }

    @GetMapping("/buscar")
    public ResponseEntity<Page<InsumoDTO.Response>> search(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) UnidadMedida unidadMedida,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(insumoService.search(nombre, unidadMedida, pageable));
    }

    @PostMapping
    public ResponseEntity<InsumoDTO.Response> create(@Valid @RequestBody InsumoDTO.Request req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(insumoService.create(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InsumoDTO.Response> update(@PathVariable Long id, @Valid @RequestBody InsumoDTO.Request req) {
        return ResponseEntity.ok(insumoService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        insumoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

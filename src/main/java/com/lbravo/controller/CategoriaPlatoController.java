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

import com.lbravo.dto.CategoriaPlatoDTO;
import com.lbravo.service.CategoriaPlatoService;
import com.lbravo.types.AreaImpresion;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/categorias-plato")
@AllArgsConstructor
public class CategoriaPlatoController {

    private final CategoriaPlatoService categoriaPlatoService;

    @GetMapping
    public ResponseEntity<Page<CategoriaPlatoDTO.Response>> getAll(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(categoriaPlatoService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaPlatoDTO.Response> getById(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaPlatoService.getById(id));
    }

    @GetMapping("/area/{area}")
    public ResponseEntity<Page<CategoriaPlatoDTO.Response>> getByArea(
            @PathVariable AreaImpresion area,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(categoriaPlatoService.getByAreaImpresion(area, pageable));
    }

    @GetMapping("/buscar")
    public ResponseEntity<Page<CategoriaPlatoDTO.Response>> search(
            @RequestParam String nombre,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(categoriaPlatoService.searchByNombre(nombre, pageable));
    }

    @PostMapping
    public ResponseEntity<CategoriaPlatoDTO.Response> create(@Valid @RequestBody CategoriaPlatoDTO.Request req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaPlatoService.create(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaPlatoDTO.Response> update(@PathVariable Long id, @Valid @RequestBody CategoriaPlatoDTO.Request req) {
        return ResponseEntity.ok(categoriaPlatoService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoriaPlatoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

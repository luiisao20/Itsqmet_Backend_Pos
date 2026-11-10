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

import com.lbravo.dto.DistribuidorDTO;
import com.lbravo.service.DistribuidorService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/distribuidores")
@AllArgsConstructor
public class DistribuidorController {

    private final DistribuidorService distribuidorService;

    @GetMapping
    public ResponseEntity<Page<DistribuidorDTO.Response>> getAll(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(distribuidorService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DistribuidorDTO.Response> getById(@PathVariable Long id) {
        return ResponseEntity.ok(distribuidorService.getById(id));
    }

    @GetMapping("/buscar")
    public ResponseEntity<Page<DistribuidorDTO.Response>> search(
            @RequestParam(required = false) String filtro,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(distribuidorService.search(filtro, pageable));
    }

    @PostMapping
    public ResponseEntity<DistribuidorDTO.Response> create(@Valid @RequestBody DistribuidorDTO.Request req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(distribuidorService.create(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DistribuidorDTO.Response> update(@PathVariable Long id, @Valid @RequestBody DistribuidorDTO.Request req) {
        return ResponseEntity.ok(distribuidorService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        distribuidorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

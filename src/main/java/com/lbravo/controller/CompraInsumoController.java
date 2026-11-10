package com.lbravo.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lbravo.dto.CompraInsumoDTO;
import com.lbravo.service.CompraInsumoService;
import com.lbravo.types.EstadoCompra;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/compras")
@AllArgsConstructor
public class CompraInsumoController {

    private final CompraInsumoService compraInsumoService;

    @GetMapping
    public ResponseEntity<Page<CompraInsumoDTO.Response>> getAll(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(compraInsumoService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompraInsumoDTO.Response> getById(@PathVariable Long id) {
        return ResponseEntity.ok(compraInsumoService.getById(id));
    }

    @GetMapping("/buscar")
    public ResponseEntity<Page<CompraInsumoDTO.Response>> search(
            @RequestParam(required = false) String numeroFactura,
            @RequestParam(required = false) Long idDistribuidor,
            @RequestParam(required = false) EstadoCompra estado,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(compraInsumoService.search(numeroFactura, idDistribuidor, estado, pageable));
    }

    @PostMapping
    public ResponseEntity<CompraInsumoDTO.Response> registrarCompra(@Valid @RequestBody CompraInsumoDTO.Request req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(compraInsumoService.registrarCompra(req));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<CompraInsumoDTO.Response> updateEstado(
            @PathVariable Long id,
            @RequestParam EstadoCompra estado) {
        return ResponseEntity.ok(compraInsumoService.updateEstado(id, estado));
    }
}

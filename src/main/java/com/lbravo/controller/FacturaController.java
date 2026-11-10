package com.lbravo.controller;

import java.time.OffsetDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
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

import com.lbravo.dto.FacturaDTO;
import com.lbravo.service.FacturaService;
import com.lbravo.types.EstadoFactura;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/facturas")
@AllArgsConstructor
public class FacturaController {

    private final FacturaService facturaService;

    @GetMapping
    public ResponseEntity<Page<FacturaDTO.Response>> getAll(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(facturaService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FacturaDTO.Response> getById(@PathVariable Long id) {
        return ResponseEntity.ok(facturaService.getById(id));
    }

    @GetMapping("/buscar")
    public ResponseEntity<Page<FacturaDTO.Response>> search(
            @RequestParam(required = false) String secuencial,
            @RequestParam(required = false) String clienteFiltro,
            @RequestParam(required = false) EstadoFactura estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime hasta,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(facturaService.search(secuencial, clienteFiltro, estado, desde, hasta, pageable));
    }

    @PostMapping
    public ResponseEntity<FacturaDTO.Response> emitirFactura(@Valid @RequestBody FacturaDTO.Request req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(facturaService.emitirFactura(req));
    }

    @PatchMapping("/{id}/anular")
    public ResponseEntity<FacturaDTO.Response> anularFactura(@PathVariable Long id) {
        return ResponseEntity.ok(facturaService.anularFactura(id));
    }
}

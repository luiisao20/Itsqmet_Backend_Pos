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

import com.lbravo.dto.PagoDTO;
import com.lbravo.service.PagoService;
import com.lbravo.types.MetodoPago;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/pagos")
@AllArgsConstructor
public class PagoController {

    private final PagoService pagoService;

    @GetMapping
    public ResponseEntity<Page<PagoDTO.Response>> getAll(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(pagoService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagoDTO.Response> getById(@PathVariable Long id) {
        return ResponseEntity.ok(pagoService.getById(id));
    }

    @GetMapping("/factura/{idFactura}")
    public ResponseEntity<Page<PagoDTO.Response>> getByFactura(
            @PathVariable Long idFactura,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(pagoService.getByFactura(idFactura, pageable));
    }

    @GetMapping("/metodo/{metodoPago}")
    public ResponseEntity<Page<PagoDTO.Response>> getByMetodoPago(
            @PathVariable MetodoPago metodoPago,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(pagoService.getByMetodoPago(metodoPago, pageable));
    }

    @PostMapping
    public ResponseEntity<PagoDTO.Response> registrarPago(@Valid @RequestBody PagoDTO.Request req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pagoService.registrarPago(req));
    }
}

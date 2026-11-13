package com.lbravo.controller;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lbravo.dto.ReporteDTO;
import com.lbravo.service.ReporteService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/reportes")
@AllArgsConstructor
public class ReporteController {

  private final ReporteService reporteService;

  @GetMapping("/ventas")
  public ResponseEntity<ReporteDTO.VentaResumen> getResumenVentas(
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime desde,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime hasta) {
    return ResponseEntity.ok(reporteService.getResumenVentas(desde, hasta));
  }

  @GetMapping("/platos-populares")
  public ResponseEntity<List<ReporteDTO.PlatoPopular>> getPlatosPopulares(
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime desde,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime hasta,
      @RequestParam(defaultValue = "10") int limit) {
    return ResponseEntity.ok(reporteService.getPlatosPopulares(desde, hasta, limit));
  }

  @GetMapping("/proveedores")
  public ResponseEntity<List<ReporteDTO.GastosProveedor>> getGastosProveedores(
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime desde,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime hasta) {
    return ResponseEntity.ok(reporteService.getGastosProveedores(desde, hasta));
  }

  @GetMapping("/margenes")
  public ResponseEntity<List<ReporteDTO.MargenPlato>> getMargenesPlatos() {
    return ResponseEntity.ok(reporteService.getMargenesPlatos());
  }
}

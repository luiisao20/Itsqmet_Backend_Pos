package com.lbravo.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class ReporteDTO {

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  public static class VentaResumen {
    private String periodo;
    private BigDecimal totalFacturado;
    private long totalPedidos;
    private long pedidosSalon;
    private long pedidosLlevar;
    private BigDecimal totalPropinas;
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  public static class PlatoPopular {
    private Long idPlato;
    private String nombrePlato;
    private long totalVendido;
    private BigDecimal totalIngresos;
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  public static class GastosProveedor {
    private Long idDistribuidor;
    private String razonSocial;
    private Boolean esProveedorLocal;
    private BigDecimal totalCompras;
    private long cantidadCompras;
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  public static class MargenPlato {
    private Long idPlato;
    private String nombrePlato;
    private BigDecimal precioVenta;
    private BigDecimal costoReceta;
    private BigDecimal margenGanancia;
    private BigDecimal porcentajeMargen;
  }
}

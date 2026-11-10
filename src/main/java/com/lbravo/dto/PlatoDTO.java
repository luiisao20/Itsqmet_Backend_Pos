package com.lbravo.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class PlatoDTO {

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  public static class Request {
    @NotNull(message = "El id de categoría es obligatorio")
    private Long idCategoria;

    @NotBlank(message = "El nombre del plato es obligatorio")
    @Size(max = 150, message = "El nombre no puede exceder 150 caracteres")
    private String nombre;

    private String descripcion;

    @NotNull(message = "El precio unitario es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio unitario debe ser mayor a 0")
    private BigDecimal precioUnitario;

    private Boolean aplicaIva;

    private Boolean disponible;

    @Size(max = 500, message = "La URL de la imagen no puede exceder 500 caracteres")
    private String urlImagen;
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  public static class Response {
    private Long id;
    private Long idCategoria;
    private String nombreCategoria;
    private String nombre;
    private String descripcion;
    private BigDecimal precioUnitario;
    private Boolean aplicaIva;
    private Boolean disponible;
    private String urlImagen;
    private OffsetDateTime createdAt;
  }
}

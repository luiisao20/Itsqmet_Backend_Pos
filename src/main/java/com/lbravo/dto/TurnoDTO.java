package com.lbravo.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class TurnoDTO {

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  public static class Request {
    @NotBlank(message = "El id de usuario es obligatorio")
    private String idUsuario;

    private String observaciones;
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  public static class Response {
    private Long id;
    private String idUsuario;
    private String nombreUsuario;
    private String rolUsuario;
    private OffsetDateTime fechaInicio;
    private OffsetDateTime fechaFin;
    private BigDecimal totalHoras;
    private String observaciones;
    private OffsetDateTime createdAt;
  }
}

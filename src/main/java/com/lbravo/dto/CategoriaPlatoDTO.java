package com.lbravo.dto;

import java.time.OffsetDateTime;

import com.lbravo.types.AreaImpresion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class CategoriaPlatoDTO {

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  public static class Request {
    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;

    @NotNull(message = "El área de impresión es obligatoria")
    private AreaImpresion areaImpresion;

    private Integer ordenVisual;
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  public static class Response {
    private Long id;
    private String nombre;
    private AreaImpresion areaImpresion;
    private Integer ordenVisual;
    private OffsetDateTime createdAt;
  }
}

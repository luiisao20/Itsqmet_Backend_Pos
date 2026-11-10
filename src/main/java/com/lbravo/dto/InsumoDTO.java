package com.lbravo.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.lbravo.types.UnidadMedida;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class InsumoDTO {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request {
        @NotBlank(message = "El nombre del insumo es obligatorio")
        @Size(max = 150, message = "El nombre no puede exceder 150 caracteres")
        private String nombre;

        @NotNull(message = "La unidad de medida es obligatoria")
        private UnidadMedida unidadMedida;

        @NotNull(message = "El stock actual es obligatorio")
        @DecimalMin(value = "0.0", message = "El stock actual no puede ser negativo")
        private BigDecimal stockActual;

        @NotNull(message = "El stock mínimo es obligatorio")
        @DecimalMin(value = "0.0", message = "El stock mínimo no puede ser negativo")
        private BigDecimal stockMinimo;

        @NotNull(message = "El costo promedio es obligatorio")
        @DecimalMin(value = "0.0", message = "El costo promedio no puede ser negativo")
        private BigDecimal costoPromedio;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String nombre;
        private UnidadMedida unidadMedida;
        private BigDecimal stockActual;
        private BigDecimal stockMinimo;
        private BigDecimal costoPromedio;
        private Boolean stockBajo;
        private OffsetDateTime createdAt;
    }
}

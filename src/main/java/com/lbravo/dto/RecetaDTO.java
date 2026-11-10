package com.lbravo.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.lbravo.types.UnidadMedida;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class RecetaDTO {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request {
        @NotNull(message = "El id del plato es obligatorio")
        private Long idPlato;

        @NotNull(message = "El id del insumo es obligatorio")
        private Long idInsumo;

        @NotNull(message = "La cantidad requerida es obligatoria")
        @DecimalMin(value = "0.001", message = "La cantidad requerida debe ser mayor a 0")
        private BigDecimal cantidadRequerida;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private Long idPlato;
        private String nombrePlato;
        private Long idInsumo;
        private String nombreInsumo;
        private UnidadMedida unidadMedidaInsumo;
        private BigDecimal cantidadRequerida;
        private OffsetDateTime createdAt;
    }
}

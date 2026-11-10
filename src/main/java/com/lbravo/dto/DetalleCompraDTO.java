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

public class DetalleCompraDTO {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request {
        @NotNull(message = "El id del insumo es obligatorio")
        private Long idInsumo;

        @NotNull(message = "La cantidad es obligatoria")
        @DecimalMin(value = "0.001", message = "La cantidad debe ser mayor a 0")
        private BigDecimal cantidad;

        @NotNull(message = "El costo unitario es obligatorio")
        @DecimalMin(value = "0.0001", message = "El costo unitario debe ser mayor a 0")
        private BigDecimal costoUnitario;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private Long idCompra;
        private Long idInsumo;
        private String nombreInsumo;
        private UnidadMedida unidadMedida;
        private BigDecimal cantidad;
        private BigDecimal costoUnitario;
        private BigDecimal subtotal;
        private OffsetDateTime createdAt;
    }
}

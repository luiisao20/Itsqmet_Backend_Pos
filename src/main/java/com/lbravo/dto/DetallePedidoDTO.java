package com.lbravo.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.lbravo.types.EstadoDetallePedido;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class DetallePedidoDTO {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request {
        @NotNull(message = "El id del plato es obligatorio")
        private Long idPlato;

        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser al menos 1")
        private Integer cantidad;

        @Size(max = 255, message = "Las notas de preparación no pueden exceder 255 caracteres")
        private String notasPreparacion;

        private EstadoDetallePedido estado;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private Long idPedido;
        private Long idPlato;
        private String nombrePlato;
        private Integer cantidad;
        private BigDecimal precioUnitario;
        private BigDecimal subtotal;
        private String notasPreparacion;
        private EstadoDetallePedido estado;
        private OffsetDateTime createdAt;
    }
}

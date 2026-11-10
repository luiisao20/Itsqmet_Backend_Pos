package com.lbravo.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.lbravo.types.EstadoFactura;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class FacturaDTO {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request {
        @NotNull(message = "El id del pedido es obligatorio")
        private Long idPedido;

        @NotBlank(message = "El id del cliente es obligatorio")
        private String idCliente;

        @NotBlank(message = "El número secuencial es obligatorio")
        private String numeroSecuencial;

        private String claveAccesoSri;

        @DecimalMin(value = "0.0", message = "La propina no puede ser negativa")
        private BigDecimal propina;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private Long idPedido;
        private String idCliente;
        private String razonSocialCliente;
        private String identificacionCliente;
        private String idUsuario;
        private String nombreUsuario;
        private String numeroSecuencial;
        private String claveAccesoSri;
        private OffsetDateTime fechaEmision;
        private BigDecimal subtotal;
        private BigDecimal subtotalIva;
        private BigDecimal propina;
        private BigDecimal total;
        private EstadoFactura estado;
        private OffsetDateTime createdAt;
    }
}

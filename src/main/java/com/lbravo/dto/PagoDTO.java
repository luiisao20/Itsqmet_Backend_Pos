package com.lbravo.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.lbravo.types.MetodoPago;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class PagoDTO {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request {
        @NotNull(message = "El id de la factura es obligatorio")
        private Long idFactura;

        @NotNull(message = "El método de pago es obligatorio")
        private MetodoPago metodoPago;

        @NotNull(message = "El monto es obligatorio")
        @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
        private BigDecimal monto;

        @Size(max = 100, message = "La referencia de transacción no puede exceder 100 caracteres")
        private String referenciaTransaccion;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private Long idFactura;
        private String numeroSecuencialFactura;
        private MetodoPago metodoPago;
        private BigDecimal monto;
        private String referenciaTransaccion;
        private OffsetDateTime fechaPago;
        private OffsetDateTime createdAt;
    }
}

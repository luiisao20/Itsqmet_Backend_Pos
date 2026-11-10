package com.lbravo.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import com.lbravo.types.EstadoCompra;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class CompraInsumoDTO {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request {
        @NotNull(message = "El distribuidor es obligatorio")
        private Long idDistribuidor;

        @NotBlank(message = "El número de factura del proveedor es obligatorio")
        @Size(max = 50, message = "El número de factura no puede exceder 50 caracteres")
        private String numeroFacturaProveedor;

        private EstadoCompra estado;

        @NotEmpty(message = "La compra debe incluir al menos un detalle de insumo")
        @Valid
        private List<DetalleCompraDTO.Request> detalles;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private Long idDistribuidor;
        private String razonSocialDistribuidor;
        private String rucDistribuidor;
        private String idUsuario;
        private String nombreUsuario;
        private String numeroFacturaProveedor;
        private OffsetDateTime fechaCompra;
        private BigDecimal totalCompra;
        private EstadoCompra estado;
        private List<DetalleCompraDTO.Response> detalles;
        private OffsetDateTime createdAt;
    }
}

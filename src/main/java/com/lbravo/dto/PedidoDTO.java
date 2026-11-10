package com.lbravo.dto;

import java.time.OffsetDateTime;
import java.util.List;

import com.lbravo.types.EstadoPedido;
import com.lbravo.types.TipoPedido;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class PedidoDTO {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request {
        private Long idMesa;

        private String idCliente;

        private TipoPedido tipo;

        private String observaciones;

        @NotEmpty(message = "El pedido debe contener al menos un detalle")
        @Valid
        private List<DetallePedidoDTO.Request> detalles;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ClienteRequest {
        private String observaciones;

        @NotEmpty(message = "El pedido debe contener al menos un detalle")
        @Valid
        private List<DetallePedidoDTO.Request> detalles;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private Long idMesa;
        private Integer numeroMesa;
        private String idUsuario;
        private String nombreUsuario;
        private String idCliente;
        private String razonSocialCliente;
        private OffsetDateTime fechaApertura;
        private OffsetDateTime fechaCierre;
        private EstadoPedido estado;
        private TipoPedido tipo;
        private String observaciones;
        private List<DetallePedidoDTO.Response> detalles;
        private OffsetDateTime createdAt;
    }
}

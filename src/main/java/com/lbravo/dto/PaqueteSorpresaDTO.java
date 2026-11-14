package com.lbravo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;

import com.lbravo.types.EstadoPaqueteSorpresa;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class PaqueteSorpresaDTO {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request {
        @NotBlank(message = "El título es obligatorio")
        private String titulo;

        private String descripcion;

        @NotNull(message = "El precio estimado original es obligatorio")
        @DecimalMin(value = "0.01", message = "El precio estimado debe ser mayor a 0")
        private BigDecimal precioEstimadoOriginal;

        @NotNull(message = "El precio de rescate es obligatorio")
        @DecimalMin(value = "0.01", message = "El precio de rescate debe ser mayor a 0")
        private BigDecimal precioRescate;

        @NotNull(message = "La cantidad total es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser al menos 1")
        private Integer cantidadTotal;

        @NotNull(message = "La fecha disponible es obligatoria")
        private LocalDate fechaDisponible;

        @NotNull(message = "La hora de inicio de visibilidad es obligatoria")
        private LocalTime horaInicioVisibilidad;

        @NotNull(message = "La hora fin de recogida es obligatoria")
        private LocalTime horaFinRecogida;

        private String urlImagen;

        private EstadoPaqueteSorpresa estado;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String titulo;
        private String descripcion;
        private BigDecimal precioEstimadoOriginal;
        private BigDecimal precioRescate;
        private Integer cantidadTotal;
        private Integer cantidadDisponible;
        private Integer unidadesVendidas;
        private Integer porcentajeDescuento;
        private LocalDate fechaDisponible;
        private LocalTime horaInicioVisibilidad;
        private LocalTime horaFinRecogida;
        private EstadoPaqueteSorpresa estado;
        private String urlImagen;
        private Boolean disponibleAhora;
        private Long segundosParaApertura;
        private OffsetDateTime createdAt;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class RescateRequest {
        @Builder.Default
        private Integer cantidad = 1;

        private String observaciones;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class RescateResponse {
        private Long idPedido;
        private Long idPaquete;
        private String tituloPaquete;
        private Integer cantidadRescatada;
        private BigDecimal totalPagar;
        private LocalTime horaFinRecogida;
        private String estadoPedido;
        private String mensaje;
    }
}

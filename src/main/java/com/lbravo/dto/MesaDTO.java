package com.lbravo.dto;

import java.time.OffsetDateTime;

import com.lbravo.types.EstadoMesa;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class MesaDTO {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request {
        @NotNull(message = "El número de mesa es obligatorio")
        @Min(value = 1, message = "El número de mesa debe ser al menos 1")
        private Integer numeroMesa;

        @NotNull(message = "La capacidad es obligatoria")
        @Min(value = 1, message = "La capacidad debe ser de al menos 1 persona")
        private Integer capacidad;

        @Size(max = 100, message = "La ubicación no puede exceder 100 caracteres")
        private String ubicacion;

        private EstadoMesa estado;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private Integer numeroMesa;
        private Integer capacidad;
        private String ubicacion;
        private EstadoMesa estado;
        private OffsetDateTime createdAt;
    }
}

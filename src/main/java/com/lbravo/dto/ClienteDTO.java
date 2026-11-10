package com.lbravo.dto;

import java.time.OffsetDateTime;

import com.lbravo.types.TipoIdentificacion;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class ClienteDTO {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request {
        @NotNull(message = "El tipo de identificación es obligatorio")
        private TipoIdentificacion tipoIdentificacion;

        @NotBlank(message = "La identificación es obligatoria")
        @Size(max = 30, message = "La identificación no puede exceder 30 caracteres")
        private String identificacion;

        @NotBlank(message = "La razón social o nombre es obligatorio")
        @Size(max = 200, message = "La razón social no puede exceder 200 caracteres")
        private String razonSocial;

        @Size(max = 255, message = "La dirección no puede exceder 255 caracteres")
        private String direccion;

        @Size(max = 30, message = "El teléfono no puede exceder 30 caracteres")
        private String telefono;

        @Email(message = "El formato de email no es válido")
        private String email;

        private String password;

        private Boolean activo;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response {
        private String uuid;
        private TipoIdentificacion tipoIdentificacion;
        private String identificacion;
        private String razonSocial;
        private String direccion;
        private String telefono;
        private String email;
        private Boolean activo;
        private OffsetDateTime createdAt;
    }
}

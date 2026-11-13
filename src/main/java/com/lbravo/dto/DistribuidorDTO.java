package com.lbravo.dto;

import java.time.OffsetDateTime;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class DistribuidorDTO {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request {
        @NotBlank(message = "El RUC es obligatorio")
        @Size(max = 30, message = "El RUC no puede exceder 30 caracteres")
        private String ruc;

        @NotBlank(message = "La razón social es obligatoria")
        @Size(max = 200, message = "La razón social no puede exceder 200 caracteres")
        private String razonSocial;

        @Size(max = 150, message = "El nombre de contacto no puede exceder 150 caracteres")
        private String nombreContacto;

        @Size(max = 30, message = "El teléfono no puede exceder 30 caracteres")
        private String telefono;

        @Email(message = "El formato de email no es válido")
        private String email;

        @Size(max = 255, message = "La dirección no puede exceder 255 caracteres")
        private String direccion;

        @Size(max = 100, message = "La categoría de proveedor no puede exceder 100 caracteres")
        private String categoriaProveedor;

        private Boolean esProveedorLocal;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String ruc;
        private String razonSocial;
        private String nombreContacto;
        private String telefono;
        private String email;
        private String direccion;
        private String categoriaProveedor;
        private Boolean esProveedorLocal;
        private OffsetDateTime createdAt;
    }
}

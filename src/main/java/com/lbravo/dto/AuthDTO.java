package com.lbravo.dto;

import com.lbravo.types.RolUsuario;
import com.lbravo.types.TipoIdentificacion;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class AuthDTO {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LoginRequest {
        @NotBlank(message = "El email no puede estar vacío")
        @Email(message = "El formato de email no es válido")
        private String email;

        @NotBlank(message = "La contraseña no puede estar vacía")
        @Size(min = 4, message = "La contraseña debe tener al menos 4 caracteres")
        private String password;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ClienteLoginRequest {
        @NotBlank(message = "El email o identificación es obligatorio")
        private String login;

        @NotBlank(message = "La contraseña no puede estar vacía")
        private String password;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ClienteRegisterRequest {
        @NotNull(message = "El tipo de identificación es obligatorio")
        private TipoIdentificacion tipoIdentificacion;

        @NotBlank(message = "La identificación es obligatoria")
        @Size(max = 30, message = "La identificación no puede exceder 30 caracteres")
        private String identificacion;

        @NotBlank(message = "La razón social o nombre es obligatoria")
        @Size(max = 200, message = "La razón social no puede exceder 200 caracteres")
        private String razonSocial;

        @Size(max = 255, message = "La dirección no puede exceder 255 caracteres")
        private String direccion;

        @Size(max = 30, message = "El teléfono no puede exceder 30 caracteres")
        private String telefono;

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El formato de email no es válido")
        private String email;

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
        private String password;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class AuthResponse {
        private String uuid;
        private String nombre;
        private String apellido;
        private String email;
        private RolUsuario rol;
        private Boolean activo;
        private String message;
    }
}

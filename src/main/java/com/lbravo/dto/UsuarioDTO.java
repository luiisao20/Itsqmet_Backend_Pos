package com.lbravo.dto;

import java.time.OffsetDateTime;

import com.lbravo.types.RolUsuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class UsuarioDTO {

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  public static class Request {
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100, message = "El apellido no puede exceder 100 caracteres")
    private String apellido;

    @NotBlank(message = "La cédula es obligatoria")
    @Size(min = 10, max = 20, message = "La cédula debe tener entre 10 y 20 caracteres")
    private String cedula;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato de email no es válido")
    private String email;

    @Size(min = 4, message = "La contraseña debe tener al menos 4 caracteres")
    private String password;

    private RolUsuario rol;

    private Boolean activo;
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  public static class Response {
    private String uuid;
    private String nombre;
    private String apellido;
    private String cedula;
    private String email;
    private RolUsuario rol;
    private Boolean activo;
    private OffsetDateTime createdAt;
  }
}

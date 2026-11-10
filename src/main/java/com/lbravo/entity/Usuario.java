package com.lbravo.entity;

import java.time.OffsetDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lbravo.types.RolUsuario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "usuarios")
public class Usuario {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String uuid;

  @Column(nullable = false, length = 100)
  private String nombre;

  @Column(nullable = false, length = 100)
  private String apellido;

  @Column(nullable = false, unique = true, length = 20)
  private String cedula;

  @Column(nullable = false, unique = true, length = 150)
  private String email;

  @JsonIgnore
  @Column(nullable = false, columnDefinition = "text")
  private String password;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 30)
  private RolUsuario rol;

  @Column(nullable = false)
  @Builder.Default
  private Boolean activo = true;

  @CreationTimestamp
  @Column(nullable = false, updatable = false, columnDefinition = "timestamptz")
  private OffsetDateTime createdAt;
}

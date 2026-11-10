package com.lbravo.entity;

import java.time.OffsetDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lbravo.types.TipoIdentificacion;

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
@Table(name = "clientes")
public class Cliente {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String uuid;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 30)
  private TipoIdentificacion tipoIdentificacion;

  @Column(nullable = false, unique = true, length = 30)
  private String identificacion;

  @Column(nullable = false, length = 200)
  private String razonSocial;

  @Column(length = 255)
  private String direccion;

  @Column(length = 30)
  private String telefono;

  @Column(length = 150)
  private String email;

  @JsonIgnore
  @Column(columnDefinition = "text")
  private String password;

  @Column(nullable = false)
  @Builder.Default
  private Boolean activo = true;

  @CreationTimestamp
  @Column(nullable = false, updatable = false, columnDefinition = "timestamptz")
  private OffsetDateTime createdAt;
}

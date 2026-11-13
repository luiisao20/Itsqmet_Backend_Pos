package com.lbravo.entity;

import java.time.OffsetDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "distribuidores")
public class Distribuidor {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 30)
  private String ruc;

  @Column(nullable = false, length = 200)
  private String razonSocial;

  @Column(length = 150)
  private String nombreContacto;

  @Column(length = 30)
  private String telefono;

  @Column(length = 150)
  private String email;

  @Column(length = 255)
  private String direccion;

  @Column(length = 100)
  private String categoriaProveedor;

  @org.hibernate.annotations.ColumnDefault("false")
  @Builder.Default
  private Boolean esProveedorLocal = false;

  public Boolean getEsProveedorLocal() {
    return esProveedorLocal != null ? esProveedorLocal : false;
  }

  @CreationTimestamp
  @Column(nullable = false, updatable = false, columnDefinition = "timestamptz")
  private OffsetDateTime createdAt;
}

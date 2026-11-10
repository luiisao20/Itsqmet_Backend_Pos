package com.lbravo.entity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "platos")
public class Plato {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "id_categoria", nullable = false)
  private CategoriaPlato categoria;

  @Column(nullable = false, length = 150)
  private String nombre;

  @Column(columnDefinition = "text")
  private String descripcion;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal precioUnitario;

  @Column(nullable = false)
  @Builder.Default
  private Boolean aplicaIva = true;

  @Column(nullable = false)
  @Builder.Default
  private Boolean disponible = true;

  @Column(length = 500)
  private String urlImagen;

  @CreationTimestamp
  @Column(nullable = false, updatable = false, columnDefinition = "timestamptz")
  private OffsetDateTime createdAt;
}

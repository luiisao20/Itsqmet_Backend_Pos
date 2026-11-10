package com.lbravo.entity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
@Table(name = "detalles_compras")
public class DetalleCompra {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_compra", nullable = false)
  private CompraInsumo compra;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "id_insumo", nullable = false)
  private Insumo insumo;

  @Column(nullable = false, precision = 12, scale = 3)
  private BigDecimal cantidad;

  @Column(nullable = false, precision = 10, scale = 4)
  private BigDecimal costoUnitario;

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal subtotal;

  @CreationTimestamp
  @Column(nullable = false, updatable = false, columnDefinition = "timestamptz")
  private OffsetDateTime createdAt;
}

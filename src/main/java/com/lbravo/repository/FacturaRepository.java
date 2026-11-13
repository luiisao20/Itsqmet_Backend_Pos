package com.lbravo.repository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lbravo.entity.Factura;
import com.lbravo.types.EstadoFactura;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {

    Optional<Factura> findByNumeroSecuencial(String numeroSecuencial);

    boolean existsByNumeroSecuencial(String numeroSecuencial);

    Page<Factura> findByEstado(EstadoFactura estado, Pageable pageable);

    Page<Factura> findByFechaEmisionBetween(OffsetDateTime desde, OffsetDateTime hasta, Pageable pageable);

    Page<Factura> findByClienteUuid(String clienteUuid, Pageable pageable);

    @Query("SELECT f FROM Factura f WHERE "
            + "(:secuencial IS NULL OR :secuencial = '' OR LOWER(f.numeroSecuencial) LIKE LOWER(CONCAT('%', :secuencial, '%'))) AND "
            + "(:clienteFiltro IS NULL OR :clienteFiltro = '' OR (f.cliente.identificacion LIKE CONCAT('%', :clienteFiltro, '%') OR LOWER(f.cliente.razonSocial) LIKE LOWER(CONCAT('%', :clienteFiltro, '%')))) AND "
            + "(:estado IS NULL OR f.estado = :estado) AND "
            + "(:desde IS NULL OR f.fechaEmision >= :desde) AND "
            + "(:hasta IS NULL OR f.fechaEmision <= :hasta)")
    Page<Factura> searchFacturas(
            @Param("secuencial") String secuencial,
            @Param("clienteFiltro") String clienteFiltro,
            @Param("estado") EstadoFactura estado,
            @Param("desde") OffsetDateTime desde,
            @Param("hasta") OffsetDateTime hasta,
            Pageable pageable);

    @Query("SELECT COALESCE(SUM(f.total), 0) FROM Factura f WHERE f.estado = 'EMITIDA' AND f.fechaEmision BETWEEN :desde AND :hasta")
    BigDecimal sumTotalFacturado(@Param("desde") OffsetDateTime desde, @Param("hasta") OffsetDateTime hasta);

    @Query("SELECT COALESCE(SUM(f.propina), 0) FROM Factura f WHERE f.estado = 'EMITIDA' AND f.fechaEmision BETWEEN :desde AND :hasta")
    BigDecimal sumPropinas(@Param("desde") OffsetDateTime desde, @Param("hasta") OffsetDateTime hasta);

    @Query("SELECT COUNT(f) FROM Factura f WHERE f.estado = 'EMITIDA' AND f.fechaEmision BETWEEN :desde AND :hasta")
    long countFacturadas(@Param("desde") OffsetDateTime desde, @Param("hasta") OffsetDateTime hasta);
}

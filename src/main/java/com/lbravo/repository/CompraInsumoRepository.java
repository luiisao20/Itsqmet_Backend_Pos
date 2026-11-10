package com.lbravo.repository;

import java.time.OffsetDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lbravo.entity.CompraInsumo;
import com.lbravo.types.EstadoCompra;

@Repository
public interface CompraInsumoRepository extends JpaRepository<CompraInsumo, Long> {

    Page<CompraInsumo> findByDistribuidorId(Long idDistribuidor, Pageable pageable);

    Page<CompraInsumo> findByEstado(EstadoCompra estado, Pageable pageable);

    Page<CompraInsumo> findByFechaCompraBetween(OffsetDateTime desde, OffsetDateTime hasta, Pageable pageable);

    @Query("SELECT c FROM CompraInsumo c WHERE "
            + "(:numeroFactura IS NULL OR :numeroFactura = '' OR LOWER(c.numeroFacturaProveedor) LIKE LOWER(CONCAT('%', :numeroFactura, '%'))) AND "
            + "(:idDistribuidor IS NULL OR c.distribuidor.id = :idDistribuidor) AND "
            + "(:estado IS NULL OR c.estado = :estado)")
    Page<CompraInsumo> searchCompras(
            @Param("numeroFactura") String numeroFactura,
            @Param("idDistribuidor") Long idDistribuidor,
            @Param("estado") EstadoCompra estado,
            Pageable pageable);
}

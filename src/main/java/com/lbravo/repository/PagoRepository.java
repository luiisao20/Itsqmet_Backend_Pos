package com.lbravo.repository;

import java.time.OffsetDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lbravo.entity.Pago;
import com.lbravo.types.MetodoPago;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    Page<Pago> findByFacturaId(Long idFactura, Pageable pageable);

    Page<Pago> findByMetodoPago(MetodoPago metodoPago, Pageable pageable);

    Page<Pago> findByFechaPagoBetween(OffsetDateTime desde, OffsetDateTime hasta, Pageable pageable);
}

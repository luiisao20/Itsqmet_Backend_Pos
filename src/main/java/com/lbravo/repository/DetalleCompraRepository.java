package com.lbravo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lbravo.entity.DetalleCompra;

@Repository
public interface DetalleCompraRepository extends JpaRepository<DetalleCompra, Long> {

    Page<DetalleCompra> findByCompraId(Long idCompra, Pageable pageable);

    Page<DetalleCompra> findByInsumoId(Long idInsumo, Pageable pageable);
}

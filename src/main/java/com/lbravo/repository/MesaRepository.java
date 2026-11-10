package com.lbravo.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lbravo.entity.Mesa;
import com.lbravo.types.EstadoMesa;

@Repository
public interface MesaRepository extends JpaRepository<Mesa, Long> {

    Optional<Mesa> findByNumeroMesa(Integer numeroMesa);

    boolean existsByNumeroMesa(Integer numeroMesa);

    Page<Mesa> findByEstado(EstadoMesa estado, Pageable pageable);

    Page<Mesa> findByUbicacionContainingIgnoreCase(String ubicacion, Pageable pageable);
}

package com.lbravo.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.lbravo.entity.PaqueteSorpresa;
import com.lbravo.types.EstadoPaqueteSorpresa;

@Repository
public interface PaqueteSorpresaRepository extends JpaRepository<PaqueteSorpresa, Long>, JpaSpecificationExecutor<PaqueteSorpresa> {

    List<PaqueteSorpresa> findByFechaDisponibleOrderByHoraInicioVisibilidadAsc(LocalDate fechaDisponible);

    List<PaqueteSorpresa> findByFechaDisponibleAndEstadoNotOrderByHoraInicioVisibilidadAsc(
            LocalDate fechaDisponible, EstadoPaqueteSorpresa estado);
}

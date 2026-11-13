package com.lbravo.repository;

import java.time.OffsetDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lbravo.entity.Turno;

@Repository
public interface TurnoRepository extends JpaRepository<Turno, Long> {

  Page<Turno> findByUsuarioUuid(String usuarioUuid, Pageable pageable);

  Page<Turno> findByFechaInicioBetween(OffsetDateTime desde, OffsetDateTime hasta, Pageable pageable);

  Optional<Turno> findFirstByUsuarioUuidAndFechaFinIsNullOrderByFechaInicioDesc(String usuarioUuid);
}

package com.lbravo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lbravo.entity.Receta;

@Repository
public interface RecetaRepository extends JpaRepository<Receta, Long> {

    Page<Receta> findByPlatoId(Long idPlato, Pageable pageable);

    Page<Receta> findByInsumoId(Long idInsumo, Pageable pageable);

    void deleteByPlatoId(Long idPlato);
}

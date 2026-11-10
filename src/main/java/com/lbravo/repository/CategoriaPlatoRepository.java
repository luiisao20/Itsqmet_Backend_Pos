package com.lbravo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lbravo.entity.CategoriaPlato;
import com.lbravo.types.AreaImpresion;

@Repository
public interface CategoriaPlatoRepository extends JpaRepository<CategoriaPlato, Long> {

    Page<CategoriaPlato> findByOrderByOrdenVisualAsc(Pageable pageable);

    Page<CategoriaPlato> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);

    Page<CategoriaPlato> findByAreaImpresion(AreaImpresion areaImpresion, Pageable pageable);
}

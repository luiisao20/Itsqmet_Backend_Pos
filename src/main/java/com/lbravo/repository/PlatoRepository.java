package com.lbravo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lbravo.entity.Plato;

@Repository
public interface PlatoRepository extends JpaRepository<Plato, Long> {

    Page<Plato> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);

    Page<Plato> findByCategoriaId(Long idCategoria, Pageable pageable);

    Page<Plato> findByDisponible(Boolean disponible, Pageable pageable);

    @Query("SELECT p FROM Plato p WHERE "
            + "(:nombre IS NULL OR :nombre = '' OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) AND "
            + "(:idCategoria IS NULL OR p.categoria.id = :idCategoria) AND "
            + "(:disponible IS NULL OR p.disponible = :disponible)")
    Page<Plato> searchPlatos(
            @Param("nombre") String nombre,
            @Param("idCategoria") Long idCategoria,
            @Param("disponible") Boolean disponible,
            Pageable pageable);
}

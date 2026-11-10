package com.lbravo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lbravo.entity.Insumo;
import com.lbravo.types.UnidadMedida;

@Repository
public interface InsumoRepository extends JpaRepository<Insumo, Long> {

    Page<Insumo> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);

    Page<Insumo> findByUnidadMedida(UnidadMedida unidadMedida, Pageable pageable);

    @Query("SELECT i FROM Insumo i WHERE i.stockActual <= i.stockMinimo")
    Page<Insumo> findInsumosConStockBajo(Pageable pageable);

    @Query("SELECT i FROM Insumo i WHERE "
            + "(:nombre IS NULL OR :nombre = '' OR LOWER(i.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) AND "
            + "(:unidadMedida IS NULL OR i.unidadMedida = :unidadMedida)")
    Page<Insumo> searchInsumos(
            @Param("nombre") String nombre,
            @Param("unidadMedida") UnidadMedida unidadMedida,
            Pageable pageable);
}

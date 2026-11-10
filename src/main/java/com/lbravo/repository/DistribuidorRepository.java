package com.lbravo.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lbravo.entity.Distribuidor;

@Repository
public interface DistribuidorRepository extends JpaRepository<Distribuidor, Long> {

    Optional<Distribuidor> findByRuc(String ruc);

    boolean existsByRuc(String ruc);

    Page<Distribuidor> findByCategoriaProveedor(String categoriaProveedor, Pageable pageable);

    @Query("SELECT d FROM Distribuidor d WHERE "
            + "(:filtro IS NULL OR :filtro = '' OR "
            + "LOWER(d.razonSocial) LIKE LOWER(CONCAT('%', :filtro, '%')) OR "
            + "d.ruc LIKE CONCAT('%', :filtro, '%') OR "
            + "LOWER(d.nombreContacto) LIKE LOWER(CONCAT('%', :filtro, '%')))")
    Page<Distribuidor> searchDistribuidores(@Param("filtro") String filtro, Pageable pageable);
}

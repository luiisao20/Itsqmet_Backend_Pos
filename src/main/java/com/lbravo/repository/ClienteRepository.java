package com.lbravo.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lbravo.entity.Cliente;
import com.lbravo.types.TipoIdentificacion;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, String> {

    Optional<Cliente> findByIdentificacion(String identificacion);

    boolean existsByIdentificacion(String identificacion);

    Optional<Cliente> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT c FROM Cliente c WHERE LOWER(c.email) = LOWER(:login) OR c.identificacion = :login")
    Optional<Cliente> findByEmailOrIdentificacion(@Param("login") String login);

    Page<Cliente> findByTipoIdentificacion(TipoIdentificacion tipoIdentificacion, Pageable pageable);

    @Query("SELECT c FROM Cliente c WHERE "
            + "(:filtro IS NULL OR :filtro = '' OR "
            + "LOWER(c.razonSocial) LIKE LOWER(CONCAT('%', :filtro, '%')) OR "
            + "c.identificacion LIKE CONCAT('%', :filtro, '%') OR "
            + "LOWER(c.email) LIKE LOWER(CONCAT('%', :filtro, '%')))")
    Page<Cliente> searchClientes(@Param("filtro") String filtro, Pageable pageable);
}

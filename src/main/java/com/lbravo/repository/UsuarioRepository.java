package com.lbravo.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lbravo.entity.Usuario;
import com.lbravo.types.RolUsuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByCedula(String cedula);

    boolean existsByEmail(String email);

    boolean existsByCedula(String cedula);

    java.util.List<Usuario> findByRol(RolUsuario rol);

    Page<Usuario> findByRol(RolUsuario rol, Pageable pageable);

    @Query("SELECT u FROM Usuario u WHERE "
            + "(:query IS NULL OR :query = '' OR "
            + "LOWER(u.nombre) LIKE LOWER(CONCAT('%', :query, '%')) OR "
            + "LOWER(u.apellido) LIKE LOWER(CONCAT('%', :query, '%')) OR "
            + "LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%')) OR "
            + "u.cedula LIKE CONCAT('%', :query, '%'))")
    Page<Usuario> searchUsuarios(@Param("query") String query, Pageable pageable);
}

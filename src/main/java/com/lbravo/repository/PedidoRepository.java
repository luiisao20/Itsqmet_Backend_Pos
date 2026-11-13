package com.lbravo.repository;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lbravo.entity.Pedido;
import com.lbravo.types.EstadoPedido;
import com.lbravo.types.TipoPedido;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long>, JpaSpecificationExecutor<Pedido> {

    Page<Pedido> findByEstado(EstadoPedido estado, Pageable pageable);

    Page<Pedido> findByTipo(TipoPedido tipo, Pageable pageable);

    Page<Pedido> findByMesaId(Long idMesa, Pageable pageable);

    Page<Pedido> findByUsuarioUuid(String usuarioUuid, Pageable pageable);

    Page<Pedido> findByClienteUuid(String clienteUuid, Pageable pageable);

    Page<Pedido> findByFechaAperturaBetween(OffsetDateTime desde, OffsetDateTime hasta, Pageable pageable);

    @Query("SELECT p FROM Pedido p WHERE "
            + "(:estado IS NULL OR p.estado = :estado) AND "
            + "(:tipo IS NULL OR p.tipo = :tipo) AND "
            + "(:idMesa IS NULL OR p.mesa.id = :idMesa) AND "
            + "(:usuarioUuid IS NULL OR p.usuario.uuid = :usuarioUuid) AND "
            + "(:clienteUuid IS NULL OR (p.cliente IS NOT NULL AND p.cliente.uuid = :clienteUuid)) AND "
            + "(:desde IS NULL OR p.fechaApertura >= :desde) AND "
            + "(:hasta IS NULL OR p.fechaApertura <= :hasta)")
    Page<Pedido> searchPedidos(
            @Param("estado") EstadoPedido estado,
            @Param("tipo") TipoPedido tipo,
            @Param("idMesa") Long idMesa,
            @Param("usuarioUuid") String usuarioUuid,
            @Param("clienteUuid") String clienteUuid,
            @Param("desde") OffsetDateTime desde,
            @Param("hasta") OffsetDateTime hasta,
            Pageable pageable);

    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.estado = 'FACTURADO' AND p.tipo = :tipo AND COALESCE(p.fechaCierre, p.fechaApertura) BETWEEN :desde AND :hasta")
    long countByTipoAndFechaBetween(@Param("tipo") TipoPedido tipo, @Param("desde") OffsetDateTime desde, @Param("hasta") OffsetDateTime hasta);

    @Query("SELECT dp.plato.id, dp.plato.nombre, SUM(dp.cantidad), SUM(dp.subtotal) "
            + "FROM DetallePedido dp "
            + "WHERE dp.pedido.estado = 'FACTURADO' AND COALESCE(dp.pedido.fechaCierre, dp.pedido.fechaApertura) BETWEEN :desde AND :hasta "
            + "GROUP BY dp.plato.id, dp.plato.nombre "
            + "ORDER BY SUM(dp.cantidad) DESC")
    List<Object[]> findPlatosPopulares(@Param("desde") OffsetDateTime desde, @Param("hasta") OffsetDateTime hasta, Pageable pageable);
}

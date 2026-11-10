package com.lbravo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lbravo.entity.DetallePedido;
import com.lbravo.types.EstadoDetallePedido;

@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {

    Page<DetallePedido> findByPedidoId(Long idPedido, Pageable pageable);

    Page<DetallePedido> findByEstado(EstadoDetallePedido estado, Pageable pageable);

    Page<DetallePedido> findByPedidoIdAndEstado(Long idPedido, EstadoDetallePedido estado, Pageable pageable);
}

package com.lbravo.service;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lbravo.dto.DetallePedidoDTO;
import com.lbravo.entity.DetallePedido;
import com.lbravo.entity.Pedido;
import com.lbravo.entity.Plato;
import com.lbravo.exception.ResourceNotFoundException;
import com.lbravo.repository.DetallePedidoRepository;
import com.lbravo.repository.PedidoRepository;
import com.lbravo.repository.PlatoRepository;
import com.lbravo.types.EstadoDetallePedido;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class DetallePedidoService {

    private final DetallePedidoRepository detallePedidoRepository;
    private final PedidoRepository pedidoRepository;
    private final PlatoRepository platoRepository;

    public DetallePedidoDTO.Response mapToDto(DetallePedido d) {
        return DetallePedidoDTO.Response.builder()
                .id(d.getId())
                .idPedido(d.getPedido().getId())
                .idPlato(d.getPlato().getId())
                .nombrePlato(d.getPlato().getNombre())
                .cantidad(d.getCantidad())
                .precioUnitario(d.getPrecioUnitario())
                .subtotal(d.getSubtotal())
                .notasPreparacion(d.getNotasPreparacion())
                .estado(d.getEstado())
                .createdAt(d.getCreatedAt())
                .build();
    }

    public Page<DetallePedidoDTO.Response> getByPedido(Long idPedido, Pageable pageable) {
        return detallePedidoRepository.findByPedidoId(idPedido, pageable).map(this::mapToDto);
    }

    public Page<DetallePedidoDTO.Response> getByEstado(EstadoDetallePedido estado, Pageable pageable) {
        return detallePedidoRepository.findByEstado(estado, pageable).map(this::mapToDto);
    }

    public DetallePedidoDTO.Response getById(Long id) {
        DetallePedido d = detallePedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Detalle de pedido no encontrado con id: " + id));
        return mapToDto(d);
    }

    @Transactional
    public DetallePedidoDTO.Response addDetalle(Long idPedido, DetallePedidoDTO.Request req) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con id: " + idPedido));

        Plato plato = platoRepository.findById(req.getIdPlato())
                .orElseThrow(() -> new ResourceNotFoundException("Plato no encontrado con id: " + req.getIdPlato()));

        BigDecimal precio = plato.getPrecioUnitario();
        BigDecimal subtotal = precio.multiply(BigDecimal.valueOf(req.getCantidad()));

        DetallePedido detalle = DetallePedido.builder()
                .pedido(pedido)
                .plato(plato)
                .cantidad(req.getCantidad())
                .precioUnitario(precio)
                .subtotal(subtotal)
                .notasPreparacion(req.getNotasPreparacion())
                .estado(req.getEstado() != null ? req.getEstado() : EstadoDetallePedido.PENDIENTE)
                .build();

        return mapToDto(detallePedidoRepository.save(detalle));
    }

    @Transactional
    public DetallePedidoDTO.Response updateEstado(Long id, EstadoDetallePedido nuevoEstado) {
        DetallePedido detalle = detallePedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Detalle de pedido no encontrado con id: " + id));
        detalle.setEstado(nuevoEstado);
        return mapToDto(detallePedidoRepository.save(detalle));
    }

    @Transactional
    public void delete(Long id) {
        DetallePedido detalle = detallePedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Detalle de pedido no encontrado con id: " + id));
        detallePedidoRepository.delete(detalle);
    }
}

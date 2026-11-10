package com.lbravo.service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lbravo.dto.DetallePedidoDTO;
import com.lbravo.dto.PedidoDTO;
import com.lbravo.entity.Cliente;
import com.lbravo.entity.DetallePedido;
import com.lbravo.entity.Mesa;
import com.lbravo.entity.Pedido;
import com.lbravo.entity.Plato;
import com.lbravo.entity.Usuario;
import com.lbravo.exception.BadRequestException;
import com.lbravo.exception.ResourceNotFoundException;
import com.lbravo.repository.ClienteRepository;
import com.lbravo.repository.MesaRepository;
import com.lbravo.repository.PedidoRepository;
import com.lbravo.repository.PlatoRepository;
import com.lbravo.repository.UsuarioRepository;
import com.lbravo.types.EstadoDetallePedido;
import com.lbravo.types.EstadoMesa;
import com.lbravo.types.EstadoPedido;
import com.lbravo.types.RolUsuario;
import com.lbravo.types.TipoPedido;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final MesaRepository mesaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final PlatoRepository platoRepository;
    private final DetallePedidoService detallePedidoService;
    private final SimpMessagingTemplate messagingTemplate;

    public PedidoDTO.Response mapToDto(Pedido p) {
        List<DetallePedidoDTO.Response> detallesDto = p.getDetalles() != null
                ? p.getDetalles().stream().map(detallePedidoService::mapToDto).toList()
                : List.of();

        return PedidoDTO.Response.builder()
                .id(p.getId())
                .idMesa(p.getMesa() != null ? p.getMesa().getId() : null)
                .numeroMesa(p.getMesa() != null ? p.getMesa().getNumeroMesa() : null)
                .idUsuario(p.getUsuario() != null ? p.getUsuario().getUuid() : null)
                .nombreUsuario(p.getUsuario() != null ? p.getUsuario().getNombre() + " " + p.getUsuario().getApellido() : "Sistema")
                .idCliente(p.getCliente() != null ? p.getCliente().getUuid() : null)
                .razonSocialCliente(p.getCliente() != null ? p.getCliente().getRazonSocial() : null)
                .fechaApertura(p.getFechaApertura())
                .fechaCierre(p.getFechaCierre())
                .estado(p.getEstado())
                .tipo(p.getTipo())
                .observaciones(p.getObservaciones())
                .detalles(detallesDto)
                .createdAt(p.getCreatedAt())
                .build();
    }

    public Page<PedidoDTO.Response> getAll(Pageable pageable) {
        return pedidoRepository.findAll(pageable).map(this::mapToDto);
    }

    public List<PedidoDTO.Response> getAllList() {
        return pedidoRepository.findAll().stream().map(this::mapToDto).toList();
    }

    public PedidoDTO.Response getById(Long id) {
        Pedido p = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con id: " + id));
        return mapToDto(p);
    }

    public Page<PedidoDTO.Response> search(EstadoPedido estado, TipoPedido tipo, Long idMesa,
            String usuarioUuid, String clienteUuid, OffsetDateTime desde, OffsetDateTime hasta, Pageable pageable) {
        return pedidoRepository.searchPedidos(estado, tipo, idMesa, usuarioUuid, clienteUuid, desde, hasta, pageable)
                .map(this::mapToDto);
    }

    @Transactional
    public PedidoDTO.Response create(PedidoDTO.Request req) {
        String authEmail = null;
        try {
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                authEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            }
        } catch (Exception ignored) {
        }

        Usuario mesero = null;
        if (authEmail != null && !authEmail.equals("anonymousUser")) {
            mesero = usuarioRepository.findByEmail(authEmail).orElse(null);
        }

        // Si no hay usuario autenticado o es dev, tomamos un usuario activo por defecto
        if (mesero == null) {
            mesero = usuarioRepository.findByRol(RolUsuario.MESERO).stream().filter(u -> Boolean.TRUE.equals(u.getActivo())).findFirst()
                    .orElseGet(() -> usuarioRepository.findAll().stream().filter(u -> Boolean.TRUE.equals(u.getActivo())).findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("No se encontró ningún usuario activo para registrar el pedido")));
        }

        TipoPedido tipo = req.getTipo() != null ? req.getTipo() : TipoPedido.SALON;
        Mesa mesa = null;
        if (tipo == TipoPedido.SALON) {
            if (req.getIdMesa() == null) {
                throw new BadRequestException("El id de la mesa es obligatorio para pedidos en salón");
            }
            mesa = mesaRepository.findById(req.getIdMesa())
                    .orElseThrow(() -> new ResourceNotFoundException("Mesa no encontrada con id: " + req.getIdMesa()));
        } else if (req.getIdMesa() != null) {
            mesa = mesaRepository.findById(req.getIdMesa()).orElse(null);
        }

        Cliente cliente = null;
        if (req.getIdCliente() != null && !req.getIdCliente().isBlank()) {
            cliente = clienteRepository.findById(req.getIdCliente())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + req.getIdCliente()));
        }

        Pedido pedido = Pedido.builder()
                .mesa(mesa)
                .usuario(mesero)
                .cliente(cliente)
                .fechaApertura(OffsetDateTime.now())
                .estado(EstadoPedido.ABIERTO)
                .tipo(tipo)
                .observaciones(req.getObservaciones())
                .detalles(new ArrayList<>())
                .build();

        if (req.getDetalles() != null && !req.getDetalles().isEmpty()) {
            for (DetallePedidoDTO.Request detReq : req.getDetalles()) {
                Plato plato = platoRepository.findById(detReq.getIdPlato())
                        .orElseThrow(() -> new ResourceNotFoundException("Plato no encontrado con id: " + detReq.getIdPlato()));

                BigDecimal subtotal = plato.getPrecioUnitario().multiply(BigDecimal.valueOf(detReq.getCantidad()));
                DetallePedido detalle = DetallePedido.builder()
                        .pedido(pedido)
                        .plato(plato)
                        .cantidad(detReq.getCantidad())
                        .precioUnitario(plato.getPrecioUnitario())
                        .subtotal(subtotal)
                        .notasPreparacion(detReq.getNotasPreparacion())
                        .estado(detReq.getEstado() != null ? detReq.getEstado() : EstadoDetallePedido.PENDIENTE)
                        .build();

                pedido.getDetalles().add(detalle);
            }
        }

        // Actualizar estado de la mesa a OCUPADA si es salón
        if (pedido.getTipo() == TipoPedido.SALON && mesa != null) {
            mesa.setEstado(EstadoMesa.OCUPADA);
            mesaRepository.save(mesa);
        }

        PedidoDTO.Response response = mapToDto(pedidoRepository.save(pedido));

        // Emisión inmediata por WebSocket
        broadcastPedido(response);

        return response;
    }

    @Transactional
    public PedidoDTO.Response createPedidoCliente(PedidoDTO.ClienteRequest req) {
        String authLogin = null;
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            authLogin = SecurityContextHolder.getContext().getAuthentication().getName();
        }
        if (authLogin == null || authLogin.equals("anonymousUser")) {
            throw new BadRequestException("Debes iniciar sesión como cliente para realizar un pedido");
        }

        final String login = authLogin;
        Cliente cliente = clienteRepository.findByEmailOrIdentificacion(login)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con: " + login));

        // Asignar un usuario del sistema (cajero o admin o mesero activo)
        Usuario usuarioSistema = usuarioRepository.findAll().stream()
                .filter(u -> Boolean.TRUE.equals(u.getActivo()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró usuario activo del sistema para registrar el pedido"));

        Pedido pedido = Pedido.builder()
                .mesa(null) // Para llevar no ocupa mesa
                .usuario(usuarioSistema)
                .cliente(cliente)
                .fechaApertura(OffsetDateTime.now())
                .estado(EstadoPedido.ABIERTO)
                .tipo(TipoPedido.LLEVAR)
                .observaciones(req.getObservaciones())
                .detalles(new ArrayList<>())
                .build();

        if (req.getDetalles() != null && !req.getDetalles().isEmpty()) {
            for (DetallePedidoDTO.Request detReq : req.getDetalles()) {
                Plato plato = platoRepository.findById(detReq.getIdPlato())
                        .orElseThrow(() -> new ResourceNotFoundException("Plato no encontrado con id: " + detReq.getIdPlato()));

                BigDecimal subtotal = plato.getPrecioUnitario().multiply(BigDecimal.valueOf(detReq.getCantidad()));
                DetallePedido detalle = DetallePedido.builder()
                        .pedido(pedido)
                        .plato(plato)
                        .cantidad(detReq.getCantidad())
                        .precioUnitario(plato.getPrecioUnitario())
                        .subtotal(subtotal)
                        .notasPreparacion(detReq.getNotasPreparacion())
                        .estado(detReq.getEstado() != null ? detReq.getEstado() : EstadoDetallePedido.PENDIENTE)
                        .build();

                pedido.getDetalles().add(detalle);
            }
        }

        PedidoDTO.Response response = mapToDto(pedidoRepository.save(pedido));
        broadcastPedido(response);
        return response;
    }

    public Page<PedidoDTO.Response> getMisPedidos(Pageable pageable) {
        String authLogin = null;
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            authLogin = SecurityContextHolder.getContext().getAuthentication().getName();
        }
        if (authLogin == null || authLogin.equals("anonymousUser")) {
            throw new BadRequestException("No hay una sesión de cliente activa");
        }

        final String login = authLogin;
        Cliente cliente = clienteRepository.findByEmailOrIdentificacion(login)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con: " + login));

        return pedidoRepository.findByClienteUuid(cliente.getUuid(), pageable).map(this::mapToDto);
    }

    @Transactional
    public PedidoDTO.Response updateEstado(Long id, EstadoPedido nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con id: " + id));

        pedido.setEstado(nuevoEstado);
        if (nuevoEstado == EstadoPedido.SERVIDO || nuevoEstado == EstadoPedido.FACTURADO || nuevoEstado == EstadoPedido.ANULADO) {
            if (nuevoEstado == EstadoPedido.FACTURADO || nuevoEstado == EstadoPedido.ANULADO) {
                pedido.setFechaCierre(OffsetDateTime.now());
                // Liberar la mesa si aplica
                if (pedido.getMesa() != null) {
                    Mesa mesa = pedido.getMesa();
                    mesa.setEstado(EstadoMesa.LIBRE);
                    mesaRepository.save(mesa);
                }
            }
        }

        PedidoDTO.Response response = mapToDto(pedidoRepository.save(pedido));

        // Emisión inmediata por WebSocket
        broadcastPedido(response);

        return response;
    }

    private void broadcastPedido(PedidoDTO.Response response) {
        try {
            messagingTemplate.convertAndSend("/topic/pedidos", response);
            messagingTemplate.convertAndSend("/topic/cocina", response);
            messagingTemplate.convertAndSend("/topic/admin", response);
        } catch (Exception e) {
            // Log warning si el broker no está conectado pero no detiene la transacción
        }
    }

    @Transactional
    public void delete(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con id: " + id));
        if (pedido.getEstado() != EstadoPedido.ANULADO && pedido.getEstado() != EstadoPedido.ABIERTO) {
            throw new BadRequestException("Solo se pueden eliminar pedidos en estado ABIERTO o ANULADO");
        }
        pedidoRepository.delete(pedido);
    }
}

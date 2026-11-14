package com.lbravo.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lbravo.dto.PaqueteSorpresaDTO;
import com.lbravo.dto.PedidoDTO;
import com.lbravo.entity.Cliente;
import com.lbravo.entity.PaqueteSorpresa;
import com.lbravo.entity.Pedido;
import com.lbravo.entity.Usuario;
import com.lbravo.exception.BadRequestException;
import com.lbravo.exception.ResourceNotFoundException;
import com.lbravo.repository.ClienteRepository;
import com.lbravo.repository.PaqueteSorpresaRepository;
import com.lbravo.repository.PedidoRepository;
import com.lbravo.repository.UsuarioRepository;
import com.lbravo.types.EstadoPaqueteSorpresa;
import com.lbravo.types.EstadoPedido;
import com.lbravo.types.TipoPedido;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaqueteSorpresaService {

    private final PaqueteSorpresaRepository paqueteSorpresaRepository;
    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final PedidoService pedidoService;

    public PaqueteSorpresaDTO.Response mapToDto(PaqueteSorpresa p) {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        boolean fechaValida = p.getFechaDisponible() != null && p.getFechaDisponible().isEqual(today);
        boolean horaInicioValida = p.getHoraInicioVisibilidad() != null && !now.isBefore(p.getHoraInicioVisibilidad());
        boolean horaFinValida = p.getHoraFinRecogida() != null && !now.isAfter(p.getHoraFinRecogida());
        boolean tieneStock = p.getCantidadDisponible() != null && p.getCantidadDisponible() > 0;

        boolean disponibleAhora = fechaValida && horaInicioValida && horaFinValida && tieneStock
                && (p.getEstado() == EstadoPaqueteSorpresa.ACTIVO || p.getEstado() == EstadoPaqueteSorpresa.PROGRAMADO);

        long segundosParaApertura = 0;
        if (fechaValida && p.getHoraInicioVisibilidad() != null && now.isBefore(p.getHoraInicioVisibilidad())) {
            segundosParaApertura = Math.max(0, Duration.between(now, p.getHoraInicioVisibilidad()).getSeconds());
        }

        int porcentajeDescuento = 0;
        if (p.getPrecioEstimadoOriginal() != null && p.getPrecioEstimadoOriginal().compareTo(BigDecimal.ZERO) > 0
                && p.getPrecioRescate() != null) {
            BigDecimal diff = p.getPrecioEstimadoOriginal().subtract(p.getPrecioRescate());
            porcentajeDescuento = diff.multiply(BigDecimal.valueOf(100))
                    .divide(p.getPrecioEstimadoOriginal(), 0, RoundingMode.HALF_UP)
                    .intValue();
        }

        int total = p.getCantidadTotal() != null ? p.getCantidadTotal() : 0;
        int disponible = p.getCantidadDisponible() != null ? p.getCantidadDisponible() : 0;
        int unidadesVendidas = Math.max(0, total - disponible);

        return PaqueteSorpresaDTO.Response.builder()
                .id(p.getId())
                .titulo(p.getTitulo())
                .descripcion(p.getDescripcion())
                .precioEstimadoOriginal(p.getPrecioEstimadoOriginal())
                .precioRescate(p.getPrecioRescate())
                .cantidadTotal(p.getCantidadTotal())
                .cantidadDisponible(p.getCantidadDisponible())
                .unidadesVendidas(unidadesVendidas)
                .porcentajeDescuento(porcentajeDescuento)
                .fechaDisponible(p.getFechaDisponible())
                .horaInicioVisibilidad(p.getHoraInicioVisibilidad())
                .horaFinRecogida(p.getHoraFinRecogida())
                .estado(p.getEstado())
                .urlImagen(p.getUrlImagen())
                .disponibleAhora(disponibleAhora)
                .segundosParaApertura(segundosParaApertura)
                .createdAt(p.getCreatedAt())
                .build();
    }

    public List<PaqueteSorpresaDTO.Response> getDisponiblesParaClientes() {
        LocalDate today = LocalDate.now();
        return paqueteSorpresaRepository
                .findByFechaDisponibleAndEstadoNotOrderByHoraInicioVisibilidadAsc(today, EstadoPaqueteSorpresa.FINALIZADO)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public List<PaqueteSorpresaDTO.Response> getGestionAdmin(LocalDate fecha) {
        LocalDate targetFecha = (fecha != null) ? fecha : LocalDate.now();
        return paqueteSorpresaRepository
                .findByFechaDisponibleOrderByHoraInicioVisibilidadAsc(targetFecha)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public PaqueteSorpresaDTO.Response getById(Long id) {
        PaqueteSorpresa p = paqueteSorpresaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paquete sorpresa no encontrado con id: " + id));
        return mapToDto(p);
    }

    @Transactional
    public PaqueteSorpresaDTO.Response create(PaqueteSorpresaDTO.Request req) {
        EstadoPaqueteSorpresa estadoInicial = req.getEstado() != null
                ? req.getEstado()
                : EstadoPaqueteSorpresa.ACTIVO;

        PaqueteSorpresa paquete = PaqueteSorpresa.builder()
                .titulo(req.getTitulo())
                .descripcion(req.getDescripcion())
                .precioEstimadoOriginal(req.getPrecioEstimadoOriginal())
                .precioRescate(req.getPrecioRescate())
                .cantidadTotal(req.getCantidadTotal())
                .cantidadDisponible(req.getCantidadTotal())
                .fechaDisponible(req.getFechaDisponible())
                .horaInicioVisibilidad(req.getHoraInicioVisibilidad())
                .horaFinRecogida(req.getHoraFinRecogida())
                .estado(estadoInicial)
                .urlImagen(req.getUrlImagen())
                .build();

        PaqueteSorpresa saved = paqueteSorpresaRepository.save(paquete);
        PaqueteSorpresaDTO.Response response = mapToDto(saved);

        // Notificar en tiempo real a clientes y personal
        messagingTemplate.convertAndSend("/topic/paquetes-sorpresa", response);

        return response;
    }

    @Transactional
    public PaqueteSorpresaDTO.Response update(Long id, PaqueteSorpresaDTO.Request req) {
        PaqueteSorpresa paquete = paqueteSorpresaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paquete sorpresa no encontrado con id: " + id));

        int diferenciaTotal = req.getCantidadTotal() - paquete.getCantidadTotal();
        int nuevaDisponible = Math.max(0, paquete.getCantidadDisponible() + diferenciaTotal);

        paquete.setTitulo(req.getTitulo());
        paquete.setDescripcion(req.getDescripcion());
        paquete.setPrecioEstimadoOriginal(req.getPrecioEstimadoOriginal());
        paquete.setPrecioRescate(req.getPrecioRescate());
        paquete.setCantidadTotal(req.getCantidadTotal());
        paquete.setCantidadDisponible(nuevaDisponible);
        paquete.setFechaDisponible(req.getFechaDisponible());
        paquete.setHoraInicioVisibilidad(req.getHoraInicioVisibilidad());
        paquete.setHoraFinRecogida(req.getHoraFinRecogida());
        paquete.setUrlImagen(req.getUrlImagen());
        if (req.getEstado() != null) {
            paquete.setEstado(req.getEstado());
        }

        PaqueteSorpresa saved = paqueteSorpresaRepository.save(paquete);
        PaqueteSorpresaDTO.Response response = mapToDto(saved);

        messagingTemplate.convertAndSend("/topic/paquetes-sorpresa", response);
        return response;
    }

    @Transactional
    public PaqueteSorpresaDTO.Response cambiarEstado(Long id, EstadoPaqueteSorpresa nuevoEstado) {
        PaqueteSorpresa paquete = paqueteSorpresaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paquete sorpresa no encontrado con id: " + id));

        paquete.setEstado(nuevoEstado);
        PaqueteSorpresa saved = paqueteSorpresaRepository.save(paquete);
        PaqueteSorpresaDTO.Response response = mapToDto(saved);

        messagingTemplate.convertAndSend("/topic/paquetes-sorpresa", response);
        return response;
    }

    @Transactional
    public PaqueteSorpresaDTO.RescateResponse rescatarPaquete(Long id, PaqueteSorpresaDTO.RescateRequest req) {
        String authLogin = null;
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            authLogin = SecurityContextHolder.getContext().getAuthentication().getName();
        }
        if (authLogin == null || authLogin.equals("anonymousUser")) {
            throw new BadRequestException("Debes iniciar sesión como cliente para rescatar un paquete sorpresa");
        }

        final String login = authLogin;
        Cliente cliente = clienteRepository.findByEmailOrIdentificacion(login)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con: " + login));

        PaqueteSorpresa paquete = paqueteSorpresaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paquete sorpresa no encontrado con id: " + id));

        int cantidad = (req != null && req.getCantidad() != null && req.getCantidad() > 0) ? req.getCantidad() : 1;

        if (paquete.getEstado() == EstadoPaqueteSorpresa.FINALIZADO) {
            throw new BadRequestException("Este paquete sorpresa ya no se encuentra disponible.");
        }

        if (paquete.getCantidadDisponible() < cantidad) {
            throw new BadRequestException("No hay stock suficiente para este paquete. Disponibles: " + paquete.getCantidadDisponible());
        }

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        if (paquete.getFechaDisponible().isAfter(today)
                || (paquete.getFechaDisponible().isEqual(today) && now.isBefore(paquete.getHoraInicioVisibilidad()))) {
            throw new BadRequestException("Este paquete aún no está disponible para rescatar. Se habilitará a las "
                    + paquete.getHoraInicioVisibilidad());
        }

        if (paquete.getFechaDisponible().isBefore(today)
                || (paquete.getFechaDisponible().isEqual(today) && now.isAfter(paquete.getHoraFinRecogida()))) {
            throw new BadRequestException("El horario de rescate para este paquete ha finalizado (límite: "
                    + paquete.getHoraFinRecogida() + ").");
        }

        // Decrementar stock
        paquete.setCantidadDisponible(paquete.getCantidadDisponible() - cantidad);
        if (paquete.getCantidadDisponible() == 0) {
            paquete.setEstado(EstadoPaqueteSorpresa.AGOTADO);
        }
        paqueteSorpresaRepository.save(paquete);

        // Asignar un usuario activo del sistema para registrar el pedido
        Usuario usuarioSistema = usuarioRepository.findAll().stream()
                .filter(u -> Boolean.TRUE.equals(u.getActivo()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró usuario activo del sistema"));

        String obs = "[RESCATE ODS 12] " + paquete.getTitulo()
                + " (Cantidad: " + cantidad + " pack" + (cantidad > 1 ? "s" : "") + ")"
                + (req != null && req.getObservaciones() != null && !req.getObservaciones().isBlank()
                        ? " - Obs: " + req.getObservaciones() : "")
                + " | Recogida hasta: " + paquete.getHoraFinRecogida();

        Pedido pedido = Pedido.builder()
                .mesa(null) // Para llevar
                .usuario(usuarioSistema)
                .cliente(cliente)
                .paqueteSorpresa(paquete)
                .fechaApertura(OffsetDateTime.now())
                .estado(EstadoPedido.ABIERTO)
                .tipo(TipoPedido.LLEVAR)
                .observaciones(obs)
                .detalles(new ArrayList<>())
                .build();

        Pedido savedPedido = pedidoRepository.save(pedido);

        // Notificar por WebSockets
        PaqueteSorpresaDTO.Response packDto = mapToDto(paquete);
        messagingTemplate.convertAndSend("/topic/paquetes-sorpresa", packDto);

        PedidoDTO.Response pedidoDto = pedidoService.mapToDto(savedPedido);
        messagingTemplate.convertAndSend("/topic/pedidos", pedidoDto);
        messagingTemplate.convertAndSend("/topic/cocina", pedidoDto);
        messagingTemplate.convertAndSend("/topic/admin", pedidoDto);

        BigDecimal total = paquete.getPrecioRescate().multiply(BigDecimal.valueOf(cantidad));

        return PaqueteSorpresaDTO.RescateResponse.builder()
                .idPedido(savedPedido.getId())
                .idPaquete(paquete.getId())
                .tituloPaquete(paquete.getTitulo())
                .cantidadRescatada(cantidad)
                .totalPagar(total)
                .horaFinRecogida(paquete.getHoraFinRecogida())
                .estadoPedido(savedPedido.getEstado().name())
                .mensaje("¡Paquete sorpresa rescatado con éxito! Retíralo antes de las " + paquete.getHoraFinRecogida())
                .build();
    }
}

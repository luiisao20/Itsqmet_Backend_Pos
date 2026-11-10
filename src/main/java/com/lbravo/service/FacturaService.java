package com.lbravo.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lbravo.dto.FacturaDTO;
import com.lbravo.entity.Cliente;
import com.lbravo.entity.DetallePedido;
import com.lbravo.entity.Factura;
import com.lbravo.entity.Mesa;
import com.lbravo.entity.Pedido;
import com.lbravo.entity.Usuario;
import com.lbravo.exception.BadRequestException;
import com.lbravo.exception.ResourceNotFoundException;
import com.lbravo.repository.ClienteRepository;
import com.lbravo.repository.FacturaRepository;
import com.lbravo.repository.MesaRepository;
import com.lbravo.repository.PedidoRepository;
import com.lbravo.repository.UsuarioRepository;
import com.lbravo.types.EstadoFactura;
import com.lbravo.types.EstadoMesa;
import com.lbravo.types.EstadoPedido;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class FacturaService {

    private final FacturaRepository facturaRepository;
    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final MesaRepository mesaRepository;

    public FacturaDTO.Response mapToDto(Factura f) {
        return FacturaDTO.Response.builder()
                .id(f.getId())
                .idPedido(f.getPedido().getId())
                .idCliente(f.getCliente().getUuid())
                .razonSocialCliente(f.getCliente().getRazonSocial())
                .identificacionCliente(f.getCliente().getIdentificacion())
                .idUsuario(f.getUsuario().getUuid())
                .nombreUsuario(f.getUsuario().getNombre() + " " + f.getUsuario().getApellido())
                .numeroSecuencial(f.getNumeroSecuencial())
                .claveAccesoSri(f.getClaveAccesoSri())
                .fechaEmision(f.getFechaEmision())
                .subtotal(f.getSubtotal())
                .subtotalIva(f.getSubtotalIva())
                .propina(f.getPropina())
                .total(f.getTotal())
                .estado(f.getEstado())
                .createdAt(f.getCreatedAt())
                .build();
    }

    public Page<FacturaDTO.Response> getAll(Pageable pageable) {
        return facturaRepository.findAll(pageable).map(this::mapToDto);
    }

    public FacturaDTO.Response getById(Long id) {
        Factura f = facturaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Factura no encontrada con id: " + id));
        return mapToDto(f);
    }

    public Page<FacturaDTO.Response> search(String secuencial, String clienteFiltro,
            EstadoFactura estado, OffsetDateTime desde, OffsetDateTime hasta, Pageable pageable) {
        return facturaRepository.searchFacturas(secuencial, clienteFiltro, estado, desde, hasta, pageable)
                .map(this::mapToDto);
    }

    @Transactional
    public FacturaDTO.Response emitirFactura(FacturaDTO.Request req) {
        String authEmail = null;
        try {
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                authEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            }
        } catch (Exception ignored) {
        }

        Usuario cajero = null;
        if (authEmail != null && !authEmail.equals("anonymousUser")) {
            cajero = usuarioRepository.findByEmail(authEmail).orElse(null);
        }
        if (cajero == null) {
            cajero = usuarioRepository.findAll().stream().filter(u -> Boolean.TRUE.equals(u.getActivo())).findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("No se encontró usuario activo para emitir la factura"));
        }

        Pedido pedido = pedidoRepository.findById(req.getIdPedido())
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con id: " + req.getIdPedido()));

        Cliente cliente = clienteRepository.findById(req.getIdCliente())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + req.getIdCliente()));

        if (facturaRepository.existsByNumeroSecuencial(req.getNumeroSecuencial().trim())) {
            throw new BadRequestException("Ya existe una factura con el número secuencial: " + req.getNumeroSecuencial());
        }

        // Calcular subtotal y subtotal con IVA (IVA 15% estándar en Ecuador)
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal baseImponibleIva = BigDecimal.ZERO;

        for (DetallePedido det : pedido.getDetalles()) {
            subtotal = subtotal.add(det.getSubtotal());
            if (Boolean.TRUE.equals(det.getPlato().getAplicaIva())) {
                baseImponibleIva = baseImponibleIva.add(det.getSubtotal());
            }
        }

        BigDecimal ivaPorcentaje = new BigDecimal("0.15");
        BigDecimal subtotalIva = baseImponibleIva.multiply(ivaPorcentaje).setScale(2, RoundingMode.HALF_UP);
        BigDecimal propina = req.getPropina() != null ? req.getPropina() : BigDecimal.ZERO;
        BigDecimal total = subtotal.add(subtotalIva).add(propina).setScale(2, RoundingMode.HALF_UP);

        Factura factura = Factura.builder()
                .pedido(pedido)
                .cliente(cliente)
                .usuario(cajero)
                .numeroSecuencial(req.getNumeroSecuencial().trim())
                .claveAccesoSri(req.getClaveAccesoSri())
                .fechaEmision(OffsetDateTime.now())
                .subtotal(subtotal.setScale(2, RoundingMode.HALF_UP))
                .subtotalIva(subtotalIva)
                .propina(propina)
                .total(total)
                .estado(EstadoFactura.EMITIDA)
                .build();

        Factura savedFactura = facturaRepository.save(factura);

        // Marcar pedido como FACTURADO y liberar mesa si aplica
        pedido.setEstado(EstadoPedido.FACTURADO);
        pedido.setFechaCierre(OffsetDateTime.now());
        pedidoRepository.save(pedido);

        if (pedido.getMesa() != null) {
            Mesa mesa = pedido.getMesa();
            mesa.setEstado(EstadoMesa.LIBRE);
            mesaRepository.save(mesa);
        }

        return mapToDto(savedFactura);
    }

    public Page<FacturaDTO.Response> getMisFacturas(Pageable pageable) {
        String authLogin = null;
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            authLogin = SecurityContextHolder.getContext().getAuthentication().getName();
        }
        if (authLogin == null || authLogin.equals("anonymousUser")) {
            throw new BadRequestException("No hay una sesión de cliente activa");
        }

        final String finalLogin = authLogin;
        Cliente cliente = clienteRepository.findByEmailOrIdentificacion(authLogin)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con: " + finalLogin));

        return facturaRepository.findByClienteUuid(cliente.getUuid(), pageable).map(this::mapToDto);
    }

    @Transactional
    public FacturaDTO.Response anularFactura(Long id) {
        Factura factura = facturaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Factura no encontrada con id: " + id));

        factura.setEstado(EstadoFactura.ANULADA);
        return mapToDto(facturaRepository.save(factura));
    }
}

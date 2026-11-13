package com.lbravo.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lbravo.dto.CompraInsumoDTO;
import com.lbravo.dto.DetalleCompraDTO;
import com.lbravo.entity.CompraInsumo;
import com.lbravo.entity.DetalleCompra;
import com.lbravo.entity.Distribuidor;
import com.lbravo.entity.Insumo;
import com.lbravo.entity.Usuario;
import com.lbravo.exception.ResourceNotFoundException;
import com.lbravo.repository.CompraInsumoRepository;
import com.lbravo.repository.DistribuidorRepository;
import com.lbravo.repository.InsumoRepository;
import com.lbravo.repository.UsuarioRepository;
import com.lbravo.types.EstadoCompra;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CompraInsumoService {

    private final CompraInsumoRepository compraInsumoRepository;
    private final DistribuidorRepository distribuidorRepository;
    private final UsuarioRepository usuarioRepository;
    private final InsumoRepository insumoRepository;

    public DetalleCompraDTO.Response mapDetalleToDto(DetalleCompra d) {
        return DetalleCompraDTO.Response.builder()
                .id(d.getId())
                .idCompra(d.getCompra().getId())
                .idInsumo(d.getInsumo().getId())
                .nombreInsumo(d.getInsumo().getNombre())
                .unidadMedida(d.getInsumo().getUnidadMedida())
                .cantidad(d.getCantidad())
                .costoUnitario(d.getCostoUnitario())
                .subtotal(d.getSubtotal())
                .createdAt(d.getCreatedAt())
                .build();
    }

    public CompraInsumoDTO.Response mapToDto(CompraInsumo c) {
        List<DetalleCompraDTO.Response> detallesDto = c.getDetalles() != null
                ? c.getDetalles().stream().map(this::mapDetalleToDto).toList()
                : List.of();

        return CompraInsumoDTO.Response.builder()
                .id(c.getId())
                .idDistribuidor(c.getDistribuidor().getId())
                .razonSocialDistribuidor(c.getDistribuidor().getRazonSocial())
                .rucDistribuidor(c.getDistribuidor().getRuc())
                .esProveedorLocalDistribuidor(c.getDistribuidor() != null && Boolean.TRUE.equals(c.getDistribuidor().getEsProveedorLocal()))
                .idUsuario(c.getUsuario().getUuid())
                .nombreUsuario(c.getUsuario().getNombre() + " " + c.getUsuario().getApellido())
                .numeroFacturaProveedor(c.getNumeroFacturaProveedor())
                .fechaCompra(c.getFechaCompra())
                .totalCompra(c.getTotalCompra())
                .estado(c.getEstado())
                .detalles(detallesDto)
                .createdAt(c.getCreatedAt())
                .build();
    }

    public Page<CompraInsumoDTO.Response> getAll(Pageable pageable) {
        return compraInsumoRepository.findAll(pageable).map(this::mapToDto);
    }

    public CompraInsumoDTO.Response getById(Long id) {
        CompraInsumo c = compraInsumoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compra no encontrada con id: " + id));
        return mapToDto(c);
    }

    public Page<CompraInsumoDTO.Response> search(String numeroFactura, Long idDistribuidor, EstadoCompra estado, Pageable pageable) {
        return compraInsumoRepository.searchCompras(numeroFactura, idDistribuidor, estado, pageable).map(this::mapToDto);
    }

    @Transactional
    public CompraInsumoDTO.Response registrarCompra(CompraInsumoDTO.Request req) {
        String authEmail = null;
        try {
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                authEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            }
        } catch (Exception ignored) {
        }

        Usuario admin = null;
        if (authEmail != null && !authEmail.equals("anonymousUser")) {
            admin = usuarioRepository.findByEmail(authEmail).orElse(null);
        }
        if (admin == null) {
            admin = usuarioRepository.findAll().stream().filter(u -> Boolean.TRUE.equals(u.getActivo())).findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("No se encontró usuario activo para registrar la compra"));
        }

        Distribuidor distribuidor = distribuidorRepository.findById(req.getIdDistribuidor())
                .orElseThrow(() -> new ResourceNotFoundException("Distribuidor no encontrado con id: " + req.getIdDistribuidor()));

        EstadoCompra estado = req.getEstado() != null ? req.getEstado() : EstadoCompra.RECIBIDA;

        BigDecimal totalCompra = BigDecimal.ZERO;
        List<DetalleCompra> detalles = new ArrayList<>();

        CompraInsumo compra = CompraInsumo.builder()
                .distribuidor(distribuidor)
                .usuario(admin)
                .numeroFacturaProveedor(req.getNumeroFacturaProveedor().trim())
                .fechaCompra(OffsetDateTime.now())
                .totalCompra(BigDecimal.ZERO)
                .estado(estado)
                .detalles(detalles)
                .build();

        for (DetalleCompraDTO.Request detReq : req.getDetalles()) {
            Insumo insumo = insumoRepository.findById(detReq.getIdInsumo())
                    .orElseThrow(() -> new ResourceNotFoundException("Insumo no encontrado con id: " + detReq.getIdInsumo()));

            BigDecimal subtotal = detReq.getCantidad().multiply(detReq.getCostoUnitario()).setScale(2, RoundingMode.HALF_UP);
            totalCompra = totalCompra.add(subtotal);

            DetalleCompra detalle = DetalleCompra.builder()
                    .compra(compra)
                    .insumo(insumo)
                    .cantidad(detReq.getCantidad())
                    .costoUnitario(detReq.getCostoUnitario())
                    .subtotal(subtotal)
                    .build();

            detalles.add(detalle);

            // Si la compra es recibida, actualizar stock y recalcular costo promedio ponderado
            if (estado == EstadoCompra.RECIBIDA) {
                BigDecimal stockPrevio = insumo.getStockActual();
                BigDecimal costoPrevio = insumo.getCostoPromedio();
                BigDecimal nuevoStock = stockPrevio.add(detReq.getCantidad());

                if (nuevoStock.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal valorTotal = (stockPrevio.multiply(costoPrevio)).add(subtotal);
                    BigDecimal nuevoCostoPromedio = valorTotal.divide(nuevoStock, 4, RoundingMode.HALF_UP);
                    insumo.setCostoPromedio(nuevoCostoPromedio);
                }
                insumo.setStockActual(nuevoStock);
                insumoRepository.save(insumo);
            }
        }

        compra.setTotalCompra(totalCompra.setScale(2, RoundingMode.HALF_UP));
        return mapToDto(compraInsumoRepository.save(compra));
    }

    @Transactional
    public CompraInsumoDTO.Response updateEstado(Long id, EstadoCompra nuevoEstado) {
        CompraInsumo compra = compraInsumoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compra no encontrada con id: " + id));

        // Si cambia a RECIBIDA desde otro estado, actualizar stock
        if (compra.getEstado() != EstadoCompra.RECIBIDA && nuevoEstado == EstadoCompra.RECIBIDA) {
            for (DetalleCompra det : compra.getDetalles()) {
                Insumo insumo = det.getInsumo();
                insumo.setStockActual(insumo.getStockActual().add(det.getCantidad()));
                insumoRepository.save(insumo);
            }
        }

        compra.setEstado(nuevoEstado);
        return mapToDto(compraInsumoRepository.save(compra));
    }
}

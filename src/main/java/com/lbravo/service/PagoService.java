package com.lbravo.service;

import java.time.OffsetDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lbravo.dto.PagoDTO;
import com.lbravo.entity.Factura;
import com.lbravo.entity.Pago;
import com.lbravo.exception.ResourceNotFoundException;
import com.lbravo.repository.FacturaRepository;
import com.lbravo.repository.PagoRepository;
import com.lbravo.types.MetodoPago;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PagoService {

    private final PagoRepository pagoRepository;
    private final FacturaRepository facturaRepository;

    public PagoDTO.Response mapToDto(Pago p) {
        return PagoDTO.Response.builder()
                .id(p.getId())
                .idFactura(p.getFactura().getId())
                .numeroSecuencialFactura(p.getFactura().getNumeroSecuencial())
                .metodoPago(p.getMetodoPago())
                .monto(p.getMonto())
                .referenciaTransaccion(p.getReferenciaTransaccion())
                .fechaPago(p.getFechaPago())
                .createdAt(p.getCreatedAt())
                .build();
    }

    public Page<PagoDTO.Response> getAll(Pageable pageable) {
        return pagoRepository.findAll(pageable).map(this::mapToDto);
    }

    public PagoDTO.Response getById(Long id) {
        Pago p = pagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado con id: " + id));
        return mapToDto(p);
    }

    public Page<PagoDTO.Response> getByFactura(Long idFactura, Pageable pageable) {
        return pagoRepository.findByFacturaId(idFactura, pageable).map(this::mapToDto);
    }

    public Page<PagoDTO.Response> getByMetodoPago(MetodoPago metodoPago, Pageable pageable) {
        return pagoRepository.findByMetodoPago(metodoPago, pageable).map(this::mapToDto);
    }

    @Transactional
    public PagoDTO.Response registrarPago(PagoDTO.Request req) {
        Factura factura = facturaRepository.findById(req.getIdFactura())
                .orElseThrow(() -> new ResourceNotFoundException("Factura no encontrada con id: " + req.getIdFactura()));

        Pago pago = Pago.builder()
                .factura(factura)
                .metodoPago(req.getMetodoPago())
                .monto(req.getMonto())
                .referenciaTransaccion(req.getReferenciaTransaccion())
                .fechaPago(OffsetDateTime.now())
                .build();

        return mapToDto(pagoRepository.save(pago));
    }
}

package com.lbravo.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lbravo.dto.InsumoDTO;
import com.lbravo.entity.Insumo;
import com.lbravo.exception.ResourceNotFoundException;
import com.lbravo.repository.InsumoRepository;
import com.lbravo.types.UnidadMedida;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class InsumoService {

    private final InsumoRepository insumoRepository;

    public InsumoDTO.Response mapToDto(Insumo i) {
        boolean stockBajo = i.getStockActual().compareTo(i.getStockMinimo()) <= 0;
        return InsumoDTO.Response.builder()
                .id(i.getId())
                .nombre(i.getNombre())
                .unidadMedida(i.getUnidadMedida())
                .stockActual(i.getStockActual())
                .stockMinimo(i.getStockMinimo())
                .costoPromedio(i.getCostoPromedio())
                .stockBajo(stockBajo)
                .createdAt(i.getCreatedAt())
                .build();
    }

    public Page<InsumoDTO.Response> getAll(Pageable pageable) {
        return insumoRepository.findAll(pageable).map(this::mapToDto);
    }

    public InsumoDTO.Response getById(Long id) {
        Insumo i = insumoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Insumo no encontrado con id: " + id));
        return mapToDto(i);
    }

    public Page<InsumoDTO.Response> getStockBajo(Pageable pageable) {
        return insumoRepository.findInsumosConStockBajo(pageable).map(this::mapToDto);
    }

    public Page<InsumoDTO.Response> search(String nombre, UnidadMedida unidadMedida, Pageable pageable) {
        return insumoRepository.searchInsumos(nombre, unidadMedida, pageable).map(this::mapToDto);
    }

    @Transactional
    public InsumoDTO.Response create(InsumoDTO.Request req) {
        Insumo insumo = Insumo.builder()
                .nombre(req.getNombre().trim())
                .unidadMedida(req.getUnidadMedida())
                .stockActual(req.getStockActual())
                .stockMinimo(req.getStockMinimo())
                .costoPromedio(req.getCostoPromedio())
                .build();

        return mapToDto(insumoRepository.save(insumo));
    }

    @Transactional
    public InsumoDTO.Response update(Long id, InsumoDTO.Request req) {
        Insumo insumo = insumoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Insumo no encontrado con id: " + id));

        insumo.setNombre(req.getNombre().trim());
        insumo.setUnidadMedida(req.getUnidadMedida());
        insumo.setStockActual(req.getStockActual());
        insumo.setStockMinimo(req.getStockMinimo());
        insumo.setCostoPromedio(req.getCostoPromedio());

        return mapToDto(insumoRepository.save(insumo));
    }

    @Transactional
    public void delete(Long id) {
        Insumo insumo = insumoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Insumo no encontrado con id: " + id));
        insumoRepository.delete(insumo);
    }
}

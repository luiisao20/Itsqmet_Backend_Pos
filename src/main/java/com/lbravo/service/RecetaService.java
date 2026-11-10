package com.lbravo.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lbravo.dto.RecetaDTO;
import com.lbravo.entity.Insumo;
import com.lbravo.entity.Plato;
import com.lbravo.entity.Receta;
import com.lbravo.exception.ResourceNotFoundException;
import com.lbravo.repository.InsumoRepository;
import com.lbravo.repository.PlatoRepository;
import com.lbravo.repository.RecetaRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RecetaService {

    private final RecetaRepository recetaRepository;
    private final PlatoRepository platoRepository;
    private final InsumoRepository insumoRepository;

    public RecetaDTO.Response mapToDto(Receta r) {
        return RecetaDTO.Response.builder()
                .id(r.getId())
                .idPlato(r.getPlato().getId())
                .nombrePlato(r.getPlato().getNombre())
                .idInsumo(r.getInsumo().getId())
                .nombreInsumo(r.getInsumo().getNombre())
                .unidadMedidaInsumo(r.getInsumo().getUnidadMedida())
                .cantidadRequerida(r.getCantidadRequerida())
                .createdAt(r.getCreatedAt())
                .build();
    }

    public Page<RecetaDTO.Response> getAll(Pageable pageable) {
        return recetaRepository.findAll(pageable).map(this::mapToDto);
    }

    public RecetaDTO.Response getById(Long id) {
        Receta r = recetaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Receta no encontrada con id: " + id));
        return mapToDto(r);
    }

    public Page<RecetaDTO.Response> getByPlato(Long idPlato, Pageable pageable) {
        return recetaRepository.findByPlatoId(idPlato, pageable).map(this::mapToDto);
    }

    public Page<RecetaDTO.Response> getByInsumo(Long idInsumo, Pageable pageable) {
        return recetaRepository.findByInsumoId(idInsumo, pageable).map(this::mapToDto);
    }

    @Transactional
    public RecetaDTO.Response create(RecetaDTO.Request req) {
        Plato plato = platoRepository.findById(req.getIdPlato())
                .orElseThrow(() -> new ResourceNotFoundException("Plato no encontrado con id: " + req.getIdPlato()));

        Insumo insumo = insumoRepository.findById(req.getIdInsumo())
                .orElseThrow(() -> new ResourceNotFoundException("Insumo no encontrado con id: " + req.getIdInsumo()));

        Receta receta = Receta.builder()
                .plato(plato)
                .insumo(insumo)
                .cantidadRequerida(req.getCantidadRequerida())
                .build();

        return mapToDto(recetaRepository.save(receta));
    }

    @Transactional
    public RecetaDTO.Response update(Long id, RecetaDTO.Request req) {
        Receta receta = recetaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Receta no encontrada con id: " + id));

        Plato plato = platoRepository.findById(req.getIdPlato())
                .orElseThrow(() -> new ResourceNotFoundException("Plato no encontrado con id: " + req.getIdPlato()));

        Insumo insumo = insumoRepository.findById(req.getIdInsumo())
                .orElseThrow(() -> new ResourceNotFoundException("Insumo no encontrado con id: " + req.getIdInsumo()));

        receta.setPlato(plato);
        receta.setInsumo(insumo);
        receta.setCantidadRequerida(req.getCantidadRequerida());

        return mapToDto(recetaRepository.save(receta));
    }

    @Transactional
    public void delete(Long id) {
        Receta receta = recetaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Receta no encontrada con id: " + id));
        recetaRepository.delete(receta);
    }
}

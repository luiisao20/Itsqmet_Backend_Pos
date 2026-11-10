package com.lbravo.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lbravo.dto.CategoriaPlatoDTO;
import com.lbravo.entity.CategoriaPlato;
import com.lbravo.exception.ResourceNotFoundException;
import com.lbravo.repository.CategoriaPlatoRepository;
import com.lbravo.types.AreaImpresion;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CategoriaPlatoService {

    private final CategoriaPlatoRepository categoriaPlatoRepository;

    public CategoriaPlatoDTO.Response mapToDto(CategoriaPlato c) {
        return CategoriaPlatoDTO.Response.builder()
                .id(c.getId())
                .nombre(c.getNombre())
                .areaImpresion(c.getAreaImpresion())
                .ordenVisual(c.getOrdenVisual())
                .createdAt(c.getCreatedAt())
                .build();
    }

    public Page<CategoriaPlatoDTO.Response> getAll(Pageable pageable) {
        return categoriaPlatoRepository.findByOrderByOrdenVisualAsc(pageable).map(this::mapToDto);
    }

    public List<CategoriaPlatoDTO.Response> getAllList() {
        return categoriaPlatoRepository.findAll().stream().map(this::mapToDto).toList();
    }

    public CategoriaPlatoDTO.Response getById(Long id) {
        CategoriaPlato c = categoriaPlatoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría de plato no encontrada con id: " + id));
        return mapToDto(c);
    }

    public Page<CategoriaPlatoDTO.Response> searchByNombre(String nombre, Pageable pageable) {
        return categoriaPlatoRepository.findByNombreContainingIgnoreCase(nombre, pageable).map(this::mapToDto);
    }

    public Page<CategoriaPlatoDTO.Response> getByAreaImpresion(AreaImpresion area, Pageable pageable) {
        return categoriaPlatoRepository.findByAreaImpresion(area, pageable).map(this::mapToDto);
    }

    @Transactional
    public CategoriaPlatoDTO.Response create(CategoriaPlatoDTO.Request req) {
        CategoriaPlato categoria = CategoriaPlato.builder()
                .nombre(req.getNombre().trim())
                .areaImpresion(req.getAreaImpresion())
                .ordenVisual(req.getOrdenVisual() != null ? req.getOrdenVisual() : 0)
                .build();
        return mapToDto(categoriaPlatoRepository.save(categoria));
    }

    @Transactional
    public CategoriaPlatoDTO.Response update(Long id, CategoriaPlatoDTO.Request req) {
        CategoriaPlato categoria = categoriaPlatoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría de plato no encontrada con id: " + id));

        categoria.setNombre(req.getNombre().trim());
        categoria.setAreaImpresion(req.getAreaImpresion());
        if (req.getOrdenVisual() != null) {
            categoria.setOrdenVisual(req.getOrdenVisual());
        }

        return mapToDto(categoriaPlatoRepository.save(categoria));
    }

    @Transactional
    public void delete(Long id) {
        CategoriaPlato categoria = categoriaPlatoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría de plato no encontrada con id: " + id));
        categoriaPlatoRepository.delete(categoria);
    }
}

package com.lbravo.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lbravo.dto.PlatoDTO;
import com.lbravo.entity.CategoriaPlato;
import com.lbravo.entity.Plato;
import com.lbravo.exception.ResourceNotFoundException;
import com.lbravo.repository.CategoriaPlatoRepository;
import com.lbravo.repository.PlatoRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PlatoService {

  private final PlatoRepository platoRepository;
  private final CategoriaPlatoRepository categoriaPlatoRepository;

  public PlatoDTO.Response mapToDto(Plato p) {
    return PlatoDTO.Response.builder()
        .id(p.getId())
        .idCategoria(p.getCategoria().getId())
        .nombreCategoria(p.getCategoria().getNombre())
        .nombre(p.getNombre())
        .descripcion(p.getDescripcion())
        .precioUnitario(p.getPrecioUnitario())
        .aplicaIva(p.getAplicaIva())
        .disponible(p.getDisponible())
        .urlImagen(p.getUrlImagen())
        .createdAt(p.getCreatedAt())
        .build();
  }

  public Page<PlatoDTO.Response> getAll(Pageable pageable) {
    return platoRepository.findAll(pageable).map(this::mapToDto);
  }

  public List<PlatoDTO.Response> getByDisponible(Boolean disponible) {
    return platoRepository.findAll().stream()
        .filter(p -> p.getDisponible().equals(disponible))
        .map(this::mapToDto).toList();
  }

  public PlatoDTO.Response getById(Long id) {
    Plato p = platoRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Plato no encontrado con id: " + id));
    return mapToDto(p);
  }

  public Page<PlatoDTO.Response> getByCategoria(Long idCategoria, Pageable pageable) {
    return platoRepository.findByCategoriaId(idCategoria, pageable).map(this::mapToDto);
  }

  public Page<PlatoDTO.Response> getByDisponible(Boolean disponible, Pageable pageable) {
    return platoRepository.findByDisponible(disponible, pageable).map(this::mapToDto);
  }

  public Page<PlatoDTO.Response> search(String nombre, Long idCategoria, Boolean disponible, Pageable pageable) {
    return platoRepository.searchPlatos(nombre, idCategoria, disponible, pageable).map(this::mapToDto);
  }

  @Transactional
  public PlatoDTO.Response create(PlatoDTO.Request req) {
    CategoriaPlato categoria = categoriaPlatoRepository.findById(req.getIdCategoria())
        .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + req.getIdCategoria()));

    Plato plato = Plato.builder()
        .categoria(categoria)
        .nombre(req.getNombre().trim())
        .descripcion(req.getDescripcion())
        .precioUnitario(req.getPrecioUnitario())
        .aplicaIva(req.getAplicaIva() != null ? req.getAplicaIva() : true)
        .disponible(req.getDisponible() != null ? req.getDisponible() : true)
        .urlImagen(req.getUrlImagen())
        .build();

    return mapToDto(platoRepository.save(plato));
  }

  @Transactional
  public PlatoDTO.Response update(Long id, PlatoDTO.Request req) {
    Plato plato = platoRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Plato no encontrado con id: " + id));

    CategoriaPlato categoria = categoriaPlatoRepository.findById(req.getIdCategoria())
        .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + req.getIdCategoria()));

    plato.setCategoria(categoria);
    plato.setNombre(req.getNombre().trim());
    plato.setDescripcion(req.getDescripcion());
    plato.setPrecioUnitario(req.getPrecioUnitario());
    if (req.getAplicaIva() != null) {
      plato.setAplicaIva(req.getAplicaIva());
    }
    if (req.getDisponible() != null) {
      plato.setDisponible(req.getDisponible());
    }
    plato.setUrlImagen(req.getUrlImagen());

    return mapToDto(platoRepository.save(plato));
  }

  @Transactional
  public void delete(Long id) {
    Plato plato = platoRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Plato no encontrado con id: " + id));
    platoRepository.delete(plato);
  }
}

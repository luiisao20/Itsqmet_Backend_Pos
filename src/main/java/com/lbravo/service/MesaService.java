package com.lbravo.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lbravo.dto.MesaDTO;
import com.lbravo.entity.Mesa;
import com.lbravo.exception.BadRequestException;
import com.lbravo.exception.ResourceNotFoundException;
import com.lbravo.repository.MesaRepository;
import com.lbravo.types.EstadoMesa;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class MesaService {

    private final MesaRepository mesaRepository;

    public MesaDTO.Response mapToDto(Mesa m) {
        return MesaDTO.Response.builder()
                .id(m.getId())
                .numeroMesa(m.getNumeroMesa())
                .capacidad(m.getCapacidad())
                .ubicacion(m.getUbicacion())
                .estado(m.getEstado())
                .createdAt(m.getCreatedAt())
                .build();
    }

    public Page<MesaDTO.Response> getAll(Pageable pageable) {
        return mesaRepository.findAll(pageable).map(this::mapToDto);
    }

    public List<MesaDTO.Response> getAllList() {
        return mesaRepository.findAll().stream().map(this::mapToDto).toList();
    }

    public MesaDTO.Response getById(Long id) {
        Mesa m = mesaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa no encontrada con id: " + id));
        return mapToDto(m);
    }

    public Page<MesaDTO.Response> getByEstado(EstadoMesa estado, Pageable pageable) {
        return mesaRepository.findByEstado(estado, pageable).map(this::mapToDto);
    }

    public Page<MesaDTO.Response> searchByUbicacion(String ubicacion, Pageable pageable) {
        return mesaRepository.findByUbicacionContainingIgnoreCase(ubicacion, pageable).map(this::mapToDto);
    }

    @Transactional
    public MesaDTO.Response create(MesaDTO.Request req) {
        if (mesaRepository.existsByNumeroMesa(req.getNumeroMesa())) {
            throw new BadRequestException("Ya existe una mesa con el número: " + req.getNumeroMesa());
        }

        Mesa mesa = Mesa.builder()
                .numeroMesa(req.getNumeroMesa())
                .capacidad(req.getCapacidad())
                .ubicacion(req.getUbicacion())
                .estado(req.getEstado() != null ? req.getEstado() : EstadoMesa.LIBRE)
                .build();

        return mapToDto(mesaRepository.save(mesa));
    }

    @Transactional
    public MesaDTO.Response update(Long id, MesaDTO.Request req) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa no encontrada con id: " + id));

        if (!mesa.getNumeroMesa().equals(req.getNumeroMesa()) && mesaRepository.existsByNumeroMesa(req.getNumeroMesa())) {
            throw new BadRequestException("Ya existe una mesa con el número: " + req.getNumeroMesa());
        }

        mesa.setNumeroMesa(req.getNumeroMesa());
        mesa.setCapacidad(req.getCapacidad());
        mesa.setUbicacion(req.getUbicacion());
        if (req.getEstado() != null) {
            mesa.setEstado(req.getEstado());
        }

        return mapToDto(mesaRepository.save(mesa));
    }

    @Transactional
    public MesaDTO.Response updateEstado(Long id, EstadoMesa estado) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa no encontrada con id: " + id));
        mesa.setEstado(estado);
        return mapToDto(mesaRepository.save(mesa));
    }

    @Transactional
    public void delete(Long id) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa no encontrada con id: " + id));
        mesaRepository.delete(mesa);
    }
}

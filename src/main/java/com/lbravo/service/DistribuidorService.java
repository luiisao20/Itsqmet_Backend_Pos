package com.lbravo.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lbravo.dto.DistribuidorDTO;
import com.lbravo.entity.Distribuidor;
import com.lbravo.exception.BadRequestException;
import com.lbravo.exception.ResourceNotFoundException;
import com.lbravo.repository.DistribuidorRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class DistribuidorService {

    private final DistribuidorRepository distribuidorRepository;

    public DistribuidorDTO.Response mapToDto(Distribuidor d) {
        return DistribuidorDTO.Response.builder()
                .id(d.getId())
                .ruc(d.getRuc())
                .razonSocial(d.getRazonSocial())
                .nombreContacto(d.getNombreContacto())
                .telefono(d.getTelefono())
                .email(d.getEmail())
                .direccion(d.getDireccion())
                .categoriaProveedor(d.getCategoriaProveedor())
                .createdAt(d.getCreatedAt())
                .build();
    }

    public Page<DistribuidorDTO.Response> getAll(Pageable pageable) {
        return distribuidorRepository.findAll(pageable).map(this::mapToDto);
    }

    public DistribuidorDTO.Response getById(Long id) {
        Distribuidor d = distribuidorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Distribuidor no encontrado con id: " + id));
        return mapToDto(d);
    }

    public Page<DistribuidorDTO.Response> search(String filtro, Pageable pageable) {
        return distribuidorRepository.searchDistribuidores(filtro, pageable).map(this::mapToDto);
    }

    @Transactional
    public DistribuidorDTO.Response create(DistribuidorDTO.Request req) {
        String ruc = req.getRuc().trim();
        if (distribuidorRepository.existsByRuc(ruc)) {
            throw new BadRequestException("Ya existe un distribuidor con el RUC: " + ruc);
        }

        Distribuidor distribuidor = Distribuidor.builder()
                .ruc(ruc)
                .razonSocial(req.getRazonSocial().trim())
                .nombreContacto(req.getNombreContacto())
                .telefono(req.getTelefono())
                .email(req.getEmail() != null ? req.getEmail().trim().toLowerCase() : null)
                .direccion(req.getDireccion())
                .categoriaProveedor(req.getCategoriaProveedor())
                .build();

        return mapToDto(distribuidorRepository.save(distribuidor));
    }

    @Transactional
    public DistribuidorDTO.Response update(Long id, DistribuidorDTO.Request req) {
        Distribuidor distribuidor = distribuidorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Distribuidor no encontrado con id: " + id));

        String newRuc = req.getRuc().trim();
        if (!distribuidor.getRuc().equalsIgnoreCase(newRuc) && distribuidorRepository.existsByRuc(newRuc)) {
            throw new BadRequestException("Ya existe un distribuidor con el RUC: " + newRuc);
        }

        distribuidor.setRuc(newRuc);
        distribuidor.setRazonSocial(req.getRazonSocial().trim());
        distribuidor.setNombreContacto(req.getNombreContacto());
        distribuidor.setTelefono(req.getTelefono());
        distribuidor.setEmail(req.getEmail() != null ? req.getEmail().trim().toLowerCase() : null);
        distribuidor.setDireccion(req.getDireccion());
        distribuidor.setCategoriaProveedor(req.getCategoriaProveedor());

        return mapToDto(distribuidorRepository.save(distribuidor));
    }

    @Transactional
    public void delete(Long id) {
        Distribuidor distribuidor = distribuidorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Distribuidor no encontrado con id: " + id));
        distribuidorRepository.delete(distribuidor);
    }
}

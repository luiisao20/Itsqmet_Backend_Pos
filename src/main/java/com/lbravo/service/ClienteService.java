package com.lbravo.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lbravo.dto.ClienteDTO;
import com.lbravo.entity.Cliente;
import com.lbravo.exception.BadRequestException;
import com.lbravo.exception.ResourceNotFoundException;
import com.lbravo.repository.ClienteRepository;
import com.lbravo.types.TipoIdentificacion;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteDTO.Response mapToDto(Cliente c) {
        return ClienteDTO.Response.builder()
                .uuid(c.getUuid())
                .tipoIdentificacion(c.getTipoIdentificacion())
                .identificacion(c.getIdentificacion())
                .razonSocial(c.getRazonSocial())
                .direccion(c.getDireccion())
                .telefono(c.getTelefono())
                .email(c.getEmail())
                .createdAt(c.getCreatedAt())
                .build();
    }

    public Page<ClienteDTO.Response> getAll(Pageable pageable) {
        return clienteRepository.findAll(pageable).map(this::mapToDto);
    }

    public List<ClienteDTO.Response> getAllList() {
        return clienteRepository.findAll().stream().map(this::mapToDto).toList();
    }

    public ClienteDTO.Response getById(String uuid) {
        Cliente c = clienteRepository.findById(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con UUID: " + uuid));
        return mapToDto(c);
    }

    public ClienteDTO.Response getByIdentificacion(String identificacion) {
        Cliente c = clienteRepository.findByIdentificacion(identificacion)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con identificación: " + identificacion));
        return mapToDto(c);
    }

    public Page<ClienteDTO.Response> getByTipo(TipoIdentificacion tipo, Pageable pageable) {
        return clienteRepository.findByTipoIdentificacion(tipo, pageable).map(this::mapToDto);
    }

    public Page<ClienteDTO.Response> search(String filtro, Pageable pageable) {
        return clienteRepository.searchClientes(filtro, pageable).map(this::mapToDto);
    }

    @Transactional
    public ClienteDTO.Response create(ClienteDTO.Request req) {
        String iden = req.getIdentificacion().trim();
        if (clienteRepository.existsByIdentificacion(iden)) {
            throw new BadRequestException("Ya existe un cliente con la identificación: " + iden);
        }

        Cliente cliente = Cliente.builder()
                .tipoIdentificacion(req.getTipoIdentificacion())
                .identificacion(iden)
                .razonSocial(req.getRazonSocial().trim())
                .direccion(req.getDireccion() != null ? req.getDireccion().trim() : null)
                .telefono(req.getTelefono() != null ? req.getTelefono().trim() : null)
                .email(req.getEmail() != null ? req.getEmail().trim().toLowerCase() : null)
                .build();

        return mapToDto(clienteRepository.save(cliente));
    }

    @Transactional
    public ClienteDTO.Response update(String uuid, ClienteDTO.Request req) {
        Cliente cliente = clienteRepository.findById(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con UUID: " + uuid));

        String newIden = req.getIdentificacion().trim();
        if (!cliente.getIdentificacion().equalsIgnoreCase(newIden) && clienteRepository.existsByIdentificacion(newIden)) {
            throw new BadRequestException("Ya existe un cliente con la identificación: " + newIden);
        }

        cliente.setTipoIdentificacion(req.getTipoIdentificacion());
        cliente.setIdentificacion(newIden);
        cliente.setRazonSocial(req.getRazonSocial().trim());
        cliente.setDireccion(req.getDireccion() != null ? req.getDireccion().trim() : null);
        cliente.setTelefono(req.getTelefono() != null ? req.getTelefono().trim() : null);
        cliente.setEmail(req.getEmail() != null ? req.getEmail().trim().toLowerCase() : null);

        return mapToDto(clienteRepository.save(cliente));
    }

    @Transactional
    public void delete(String uuid) {
        Cliente cliente = clienteRepository.findById(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con UUID: " + uuid));
        clienteRepository.delete(cliente);
    }
}

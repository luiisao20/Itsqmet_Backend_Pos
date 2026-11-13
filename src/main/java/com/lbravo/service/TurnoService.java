package com.lbravo.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lbravo.dto.TurnoDTO;
import com.lbravo.entity.Turno;
import com.lbravo.entity.Usuario;
import com.lbravo.exception.BadRequestException;
import com.lbravo.exception.ResourceNotFoundException;
import com.lbravo.repository.TurnoRepository;
import com.lbravo.repository.UsuarioRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TurnoService {

  private final TurnoRepository turnoRepository;
  private final UsuarioRepository usuarioRepository;

  public TurnoDTO.Response mapToDto(Turno t) {
    return TurnoDTO.Response.builder()
        .id(t.getId())
        .idUsuario(t.getUsuario().getUuid())
        .nombreUsuario(t.getUsuario().getNombre() + " " + t.getUsuario().getApellido())
        .rolUsuario(t.getUsuario().getRol() != null ? t.getUsuario().getRol().name() : null)
        .fechaInicio(t.getFechaInicio())
        .fechaFin(t.getFechaFin())
        .totalHoras(t.getTotalHoras())
        .observaciones(t.getObservaciones())
        .createdAt(t.getCreatedAt())
        .build();
  }

  public Page<TurnoDTO.Response> getAll(Pageable pageable) {
    return turnoRepository.findAll(pageable).map(this::mapToDto);
  }

  public Page<TurnoDTO.Response> getByUsuario(String usuarioUuid, Pageable pageable) {
    return turnoRepository.findByUsuarioUuid(usuarioUuid, pageable).map(this::mapToDto);
  }

  public Optional<TurnoDTO.Response> getTurnoActivo(String usuarioUuid) {
    return turnoRepository.findFirstByUsuarioUuidAndFechaFinIsNullOrderByFechaInicioDesc(usuarioUuid)
        .map(this::mapToDto);
  }

  @Transactional
  public TurnoDTO.Response iniciarTurno(TurnoDTO.Request req) {
    Usuario usuario = usuarioRepository.findById(req.getIdUsuario())
        .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con uuid: " + req.getIdUsuario()));

    // Validar si ya tiene un turno abierto
    Optional<Turno> activo = turnoRepository.findFirstByUsuarioUuidAndFechaFinIsNullOrderByFechaInicioDesc(usuario.getUuid());
    if (activo.isPresent()) {
      throw new BadRequestException("El usuario ya tiene un turno activo iniciado el: " + activo.get().getFechaInicio());
    }

    Turno turno = Turno.builder()
        .usuario(usuario)
        .fechaInicio(OffsetDateTime.now())
        .observaciones(req.getObservaciones())
        .build();

    return mapToDto(turnoRepository.save(turno));
  }

  @Transactional
  public TurnoDTO.Response cerrarTurno(TurnoDTO.Request req) {
    Turno turno = turnoRepository.findFirstByUsuarioUuidAndFechaFinIsNullOrderByFechaInicioDesc(req.getIdUsuario())
        .orElseThrow(() -> new BadRequestException("El usuario no tiene un turno activo para cerrar"));

    OffsetDateTime now = OffsetDateTime.now();
    turno.setFechaFin(now);

    long seconds = Duration.between(turno.getFechaInicio(), now).getSeconds();
    double hours = Math.max(0.0, (double) seconds / 3600.0);
    turno.setTotalHoras(BigDecimal.valueOf(hours).setScale(2, RoundingMode.HALF_UP));

    if (req.getObservaciones() != null && !req.getObservaciones().isBlank()) {
      String obs = turno.getObservaciones() != null
          ? turno.getObservaciones() + " | " + req.getObservaciones().trim()
          : req.getObservaciones().trim();
      turno.setObservaciones(obs);
    }

    return mapToDto(turnoRepository.save(turno));
  }
}

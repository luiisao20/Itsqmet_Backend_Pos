package com.lbravo.controller;

import java.time.OffsetDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lbravo.dto.PedidoDTO;
import com.lbravo.service.PedidoService;
import com.lbravo.types.EstadoPedido;
import com.lbravo.types.TipoPedido;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/pedidos")
@AllArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @GetMapping
    public ResponseEntity<Page<PedidoDTO.Response>> getAll(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(pedidoService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoDTO.Response> getById(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.getById(id));
    }

    @GetMapping("/buscar")
    public ResponseEntity<Page<PedidoDTO.Response>> search(
            @RequestParam(required = false) EstadoPedido estado,
            @RequestParam(required = false) TipoPedido tipo,
            @RequestParam(required = false) Long idMesa,
            @RequestParam(required = false) String usuarioUuid,
            @RequestParam(required = false) String clienteUuid,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime hasta,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(pedidoService.search(estado, tipo, idMesa, usuarioUuid, clienteUuid, desde, hasta, pageable));
    }

    @PostMapping
    public ResponseEntity<PedidoDTO.Response> create(@Valid @RequestBody PedidoDTO.Request req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.create(req));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<PedidoDTO.Response> updateEstado(
            @PathVariable Long id,
            @RequestParam EstadoPedido estado) {
        return ResponseEntity.ok(pedidoService.updateEstado(id, estado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        pedidoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

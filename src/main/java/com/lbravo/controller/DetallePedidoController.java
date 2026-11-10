package com.lbravo.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

import com.lbravo.dto.DetallePedidoDTO;
import com.lbravo.service.DetallePedidoService;
import com.lbravo.types.EstadoDetallePedido;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/detalles-pedido")
@AllArgsConstructor
public class DetallePedidoController {

    private final DetallePedidoService detallePedidoService;

    @GetMapping("/pedido/{idPedido}")
    public ResponseEntity<Page<DetallePedidoDTO.Response>> getByPedido(
            @PathVariable Long idPedido,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(detallePedidoService.getByPedido(idPedido, pageable));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<Page<DetallePedidoDTO.Response>> getByEstado(
            @PathVariable EstadoDetallePedido estado,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(detallePedidoService.getByEstado(estado, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetallePedidoDTO.Response> getById(@PathVariable Long id) {
        return ResponseEntity.ok(detallePedidoService.getById(id));
    }

    @PostMapping("/pedido/{idPedido}")
    public ResponseEntity<DetallePedidoDTO.Response> addDetalle(
            @PathVariable Long idPedido,
            @Valid @RequestBody DetallePedidoDTO.Request req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(detallePedidoService.addDetalle(idPedido, req));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<DetallePedidoDTO.Response> updateEstado(
            @PathVariable Long id,
            @RequestParam EstadoDetallePedido estado) {
        return ResponseEntity.ok(detallePedidoService.updateEstado(id, estado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        detallePedidoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

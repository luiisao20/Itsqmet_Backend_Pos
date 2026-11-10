package com.lbravo.controller.dev;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lbravo.dto.CategoriaPlatoDTO;
import com.lbravo.dto.MesaDTO;
import com.lbravo.dto.PedidoDTO;
import com.lbravo.dto.PlatoDTO;
import com.lbravo.service.CategoriaPlatoService;
import com.lbravo.service.ClienteService;
import com.lbravo.service.MesaService;
import com.lbravo.service.PedidoService;
import com.lbravo.service.PlatoService;
import com.lbravo.types.EstadoPedido;
import com.lbravo.types.TipoPedido;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@Profile("dev")
@Controller
@RequestMapping("/dev")
@AllArgsConstructor
public class DevViewController {

    private final PedidoService pedidoService;
    private final MesaService mesaService;
    private final PlatoService platoService;
    private final CategoriaPlatoService categoriaPlatoService;
    private final ClienteService clienteService;

    @GetMapping("/pedidos/crear")
    public String vistaCrearPedido(Model model) {
        model.addAttribute("titulo", "POS Dev - Crear Pedido");
        return "dev/crear-pedido";
    }

    @GetMapping("/pedidos/monitor")
    public String vistaMonitorPedidos(Model model) {
        model.addAttribute("titulo", "POS Dev - Monitor Cocina y Administrador");
        return "dev/monitor-pedidos";
    }

    @GetMapping("/api/context")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getDevContext() {
        List<MesaDTO.Response> mesas = mesaService.getAllList();
        List<PlatoDTO.Response> platos = platoService.getByDisponible(true);
        List<CategoriaPlatoDTO.Response> categorias = categoriaPlatoService.getAllList();
        var clientes = clienteService.getAllList();

        return ResponseEntity.ok(Map.of(
                "mesas", mesas,
                "platos", platos,
                "categorias", categorias,
                "clientes", clientes,
                "tiposPedido", TipoPedido.values(),
                "estadosPedido", EstadoPedido.values()
        ));
    }

    @GetMapping("/api/pedidos/activos")
    @ResponseBody
    public ResponseEntity<List<PedidoDTO.Response>> getPedidosActivos() {
        // Pedidos activos (no facturados ni anulados) ordenados por orden de llegada (createdAt ASC)
        List<PedidoDTO.Response> pedidos = pedidoService.getAllList().stream()
                .filter(p -> p.getEstado() != EstadoPedido.FACTURADO && p.getEstado() != EstadoPedido.ANULADO)
                .sorted(Comparator.comparing(PedidoDTO.Response::getCreatedAt))
                .toList();
        return ResponseEntity.ok(pedidos);
    }

    @PostMapping("/api/pedidos")
    @ResponseBody
    public ResponseEntity<PedidoDTO.Response> crearPedidoDev(@Valid @RequestBody PedidoDTO.Request req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.create(req));
    }

    @PatchMapping("/api/pedidos/{id}/estado")
    @ResponseBody
    public ResponseEntity<PedidoDTO.Response> cambiarEstadoDev(
            @PathVariable Long id,
            @RequestParam EstadoPedido estado) {
        return ResponseEntity.ok(pedidoService.updateEstado(id, estado));
    }
}

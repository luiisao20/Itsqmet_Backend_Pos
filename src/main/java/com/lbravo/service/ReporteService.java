package com.lbravo.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lbravo.dto.ReporteDTO;
import com.lbravo.entity.Plato;
import com.lbravo.entity.Receta;
import com.lbravo.repository.CompraInsumoRepository;
import com.lbravo.repository.FacturaRepository;
import com.lbravo.repository.PedidoRepository;
import com.lbravo.repository.PlatoRepository;
import com.lbravo.repository.RecetaRepository;
import com.lbravo.types.TipoPedido;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class ReporteService {

  private final FacturaRepository facturaRepository;
  private final PedidoRepository pedidoRepository;
  private final CompraInsumoRepository compraInsumoRepository;
  private final PlatoRepository platoRepository;
  private final RecetaRepository recetaRepository;

  public ReporteDTO.VentaResumen getResumenVentas(OffsetDateTime desde, OffsetDateTime hasta) {
    OffsetDateTime d = desde != null ? desde : OffsetDateTime.now().minusDays(30);
    OffsetDateTime h = hasta != null ? hasta : OffsetDateTime.now();

    BigDecimal totalFacturado = facturaRepository.sumTotalFacturado(d, h);
    BigDecimal totalPropinas = facturaRepository.sumPropinas(d, h);
    long totalPedidos = facturaRepository.countFacturadas(d, h);
    long pedidosSalon = pedidoRepository.countByTipoAndFechaBetween(TipoPedido.SALON, d, h);
    long pedidosLlevar = pedidoRepository.countByTipoAndFechaBetween(TipoPedido.LLEVAR, d, h);

    return ReporteDTO.VentaResumen.builder()
        .periodo(d.toLocalDate() + " al " + h.toLocalDate())
        .totalFacturado(totalFacturado != null ? totalFacturado : BigDecimal.ZERO)
        .totalPedidos(totalPedidos)
        .pedidosSalon(pedidosSalon)
        .pedidosLlevar(pedidosLlevar)
        .totalPropinas(totalPropinas != null ? totalPropinas : BigDecimal.ZERO)
        .build();
  }

  public List<ReporteDTO.PlatoPopular> getPlatosPopulares(OffsetDateTime desde, OffsetDateTime hasta, int limit) {
    OffsetDateTime d = desde != null ? desde : OffsetDateTime.now().minusDays(30);
    OffsetDateTime h = hasta != null ? hasta : OffsetDateTime.now();

    List<Object[]> rawList = pedidoRepository.findPlatosPopulares(d, h, PageRequest.of(0, Math.max(limit, 10)));
    List<ReporteDTO.PlatoPopular> result = new ArrayList<>();

    for (Object[] row : rawList) {
      Long idPlato = ((Number) row[0]).longValue();
      String nombre = (String) row[1];
      long cantidad = ((Number) row[2]).longValue();
      BigDecimal ingresos = (BigDecimal) row[3];

      result.add(ReporteDTO.PlatoPopular.builder()
          .idPlato(idPlato)
          .nombrePlato(nombre)
          .totalVendido(cantidad)
          .totalIngresos(ingresos != null ? ingresos : BigDecimal.ZERO)
          .build());
    }

    return result;
  }

  public List<ReporteDTO.GastosProveedor> getGastosProveedores(OffsetDateTime desde, OffsetDateTime hasta) {
    OffsetDateTime d = desde != null ? desde : OffsetDateTime.now().minusDays(30);
    OffsetDateTime h = hasta != null ? hasta : OffsetDateTime.now();

    List<Object[]> rawList = compraInsumoRepository.findGastosPorProveedor(d, h);
    List<ReporteDTO.GastosProveedor> result = new ArrayList<>();

    for (Object[] row : rawList) {
      Long idDist = ((Number) row[0]).longValue();
      String razonSocial = (String) row[1];
      Boolean local = row[2] != null ? (Boolean) row[2] : false;
      BigDecimal totalCompras = (BigDecimal) row[3];
      long count = ((Number) row[4]).longValue();

      result.add(ReporteDTO.GastosProveedor.builder()
          .idDistribuidor(idDist)
          .razonSocial(razonSocial)
          .esProveedorLocal(local)
          .totalCompras(totalCompras != null ? totalCompras : BigDecimal.ZERO)
          .cantidadCompras(count)
          .build());
    }

    return result;
  }

  public List<ReporteDTO.MargenPlato> getMargenesPlatos() {
    List<Plato> platos = platoRepository.findAll();
    List<ReporteDTO.MargenPlato> result = new ArrayList<>();

    for (Plato p : platos) {
      List<Receta> recetas = recetaRepository.findByPlatoId(p.getId());
      BigDecimal costoReceta = BigDecimal.ZERO;

      for (Receta r : recetas) {
        if (r.getInsumo() != null && r.getInsumo().getCostoPromedio() != null) {
          BigDecimal cost = r.getInsumo().getCostoPromedio().multiply(r.getCantidadRequerida());
          costoReceta = costoReceta.add(cost);
        }
      }

      costoReceta = costoReceta.setScale(2, RoundingMode.HALF_UP);
      BigDecimal precioVenta = p.getPrecioUnitario() != null ? p.getPrecioUnitario() : BigDecimal.ZERO;
      BigDecimal margen = precioVenta.subtract(costoReceta);
      BigDecimal porcentaje = BigDecimal.ZERO;

      if (precioVenta.compareTo(BigDecimal.ZERO) > 0) {
        porcentaje = margen.divide(precioVenta, 4, RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100))
            .setScale(1, RoundingMode.HALF_UP);
      }

      result.add(ReporteDTO.MargenPlato.builder()
          .idPlato(p.getId())
          .nombrePlato(p.getNombre())
          .precioVenta(precioVenta)
          .costoReceta(costoReceta)
          .margenGanancia(margen)
          .porcentajeMargen(porcentaje)
          .build());
    }

    return result;
  }
}

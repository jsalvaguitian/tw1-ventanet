package com.tallerwebi.dominio.servicios;
import java.util.List;
import com.tallerwebi.presentacion.dto.ProductoMasCotizadoDTO;

public interface ServicioEstadisticas {
    List<ProductoMasCotizadoDTO> obtenerTopProductos(int limite);
}
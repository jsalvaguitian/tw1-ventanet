package com.tallerwebi.infraestructura;

import java.util.List;
import com.tallerwebi.presentacion.dto.ProductoMasCotizadoDTO;

public interface RepositorioEstadisticas {
    List<ProductoMasCotizadoDTO> obtenerProductosMasCotizados(int limite);
}
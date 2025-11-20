package com.tallerwebi.dominio.servicios;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tallerwebi.infraestructura.RepositorioEstadisticas;
import com.tallerwebi.presentacion.dto.ProductoMasCotizadoDTO;

@Service
public class ServicioEstadisticasImpl implements ServicioEstadisticas {
    @Autowired
    private RepositorioEstadisticas repositorioEstadisticas;

    @Override
    public List<ProductoMasCotizadoDTO> obtenerTopProductos(int limite) {
        return repositorioEstadisticas.obtenerProductosMasCotizados(limite);
    }
}
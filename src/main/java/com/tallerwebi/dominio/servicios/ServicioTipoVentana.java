package com.tallerwebi.dominio.servicios;

import java.util.List;

import com.tallerwebi.dominio.entidades.TipoVentana;
import com.tallerwebi.presentacion.dto.TipoVentanaDTO;

public interface ServicioTipoVentana {
    List<TipoVentana> obtener();
    void crearTipoVentana(TipoVentana ventana);
    List<TipoVentana> obtenerPorIdTipoProducto(Long id);

}

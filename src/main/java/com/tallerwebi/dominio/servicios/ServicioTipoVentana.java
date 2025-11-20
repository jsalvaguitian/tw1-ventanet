package com.tallerwebi.dominio.servicios;

import java.util.List;

import com.tallerwebi.dominio.entidades.TipoVentana;

public interface ServicioTipoVentana {
    List<TipoVentana> obtener();
    void crearTipoVentana(TipoVentana ventana);
    List<TipoVentana> obtenerPorIdTipoProducto(Long id);
    TipoVentana obtener(Long id);
}

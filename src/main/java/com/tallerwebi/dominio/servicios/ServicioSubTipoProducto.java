package com.tallerwebi.dominio.servicios;

import java.util.List;

import com.tallerwebi.dominio.entidades.SubTipoProducto;
import com.tallerwebi.presentacion.dto.TipoVentanaDTO;

public interface ServicioSubTipoProducto {
    List<SubTipoProducto> obtener();
    void crearTipoVentana(SubTipoProducto ventana);
    List<SubTipoProducto> obtenerPorIdTipoProducto(Long id);

}

package com.tallerwebi.dominio.servicios;

import java.util.List;

import com.tallerwebi.dominio.entidades.TipoProducto;

public interface ServicioTipoProducto {
    List<TipoProducto> obtener();
    void crearTipoProducto(TipoProducto producto);
    TipoProducto obtenerPorId(Long id);
    void actualizar(TipoProducto producto);
    void eliminar(Long id);    
} 

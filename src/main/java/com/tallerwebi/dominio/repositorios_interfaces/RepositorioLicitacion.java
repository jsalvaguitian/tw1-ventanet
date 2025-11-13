package com.tallerwebi.dominio.repositorios_interfaces;

import java.util.List;

import com.tallerwebi.dominio.entidades.Licitacion;

public interface RepositorioLicitacion {
    Licitacion obtenerPorId(Long id);
    List<Licitacion> obtenerPorIdProveedor(Long proveedorId);
    List<Licitacion> obtenerPorIdProveedorYEstado(Long proveedorId, String estado);    
    boolean actualizarEstado(Licitacion item);
    List<Licitacion> obtenerPorIdCliente(Long id);
    Licitacion guardar(Licitacion licitacion);
}

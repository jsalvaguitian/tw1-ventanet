package com.tallerwebi.dominio.repositorios_interfaces;

import java.util.List;

import com.tallerwebi.dominio.entidades.Cotizacion;

public interface RepositorioCotizacion {
    Cotizacion obtenerPorId(Long id);
    List<Cotizacion> obtenerPorIdProveedor(Long proveedorId);
    List<Cotizacion> obtenerPorIdProveedorYEstado(Long proveedorId, String estado);    
    boolean actualizarEstado(Cotizacion item);
    List<Cotizacion> obtenerPorIdCliente(Long id);
}

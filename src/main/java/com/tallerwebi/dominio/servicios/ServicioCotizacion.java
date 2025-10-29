package com.tallerwebi.dominio.servicios;

import java.util.List;

import com.tallerwebi.dominio.entidades.Cotizacion;

public interface ServicioCotizacion {
    Cotizacion obtenerPorId(Long id);
    List<Cotizacion> obtenerPorIdProveedor(Long proveedorId);
    void actualizarEstado(Long estadoId, Long cotizacionId);
    List<Cotizacion> obtenerCotizacionPorIdCliente(Long id);
}

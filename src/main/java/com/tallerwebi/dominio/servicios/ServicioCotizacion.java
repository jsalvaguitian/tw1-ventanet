package com.tallerwebi.dominio.servicios;

import java.util.List;
import java.util.Map;

import com.tallerwebi.dominio.entidades.Cliente;
import com.tallerwebi.dominio.entidades.Cotizacion;
import com.tallerwebi.dominio.enums.EstadoCotizacion;
import com.tallerwebi.dominio.excepcion.CotizacionesExistente;

public interface ServicioCotizacion {
    Cotizacion obtenerPorId(Long id);
    List<Cotizacion> obtenerPorIdProveedor(Long proveedorId);
    void actualizarEstado(Long cotizacionId, EstadoCotizacion estado)throws CotizacionesExistente;
    List<Cotizacion> obtenerCotizacionPorIdCliente(Long id);
    Cotizacion guardar(Cotizacion cotizacion);
    void registrarCotizacion(Cliente cliente, Map<Long,Integer> cotizacionItems);
}

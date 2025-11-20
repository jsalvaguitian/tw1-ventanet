package com.tallerwebi.dominio.servicios;

import java.util.List;
import java.util.Map;

import com.tallerwebi.dominio.entidades.Cliente;
import com.tallerwebi.dominio.entidades.Cotizacion;
import com.tallerwebi.dominio.enums.EstadoCotizacion;
import com.tallerwebi.dominio.excepcion.CotizacionesExistente;
import com.tallerwebi.dominio.excepcion.NoHayCotizacionExistente;

public interface ServicioCotizacion {
    Cotizacion obtenerPorId(Long id) throws NoHayCotizacionExistente;

    List<Cotizacion> obtenerPorIdProveedor(Long proveedorId);

    void actualizarEstado(Long cotizacionId, EstadoCotizacion estado) throws CotizacionesExistente;

    List<Cotizacion> obtenerCotizacionPorIdCliente(Long id) throws NoHayCotizacionExistente;

    Cotizacion guardar(Cotizacion cotizacion);

    void registrarCotizacion(Cliente cliente, Map<Long, Integer> cotizacionItems);

    Map<String, Long> obtenerEstadisticasCotizacionesDelProveedor(Long proveedorId);

    Map<String, Object> obtenerEstadisticaComparacionEntreProveedores(Long proveedorId);

    Map<String, Long> obtenerProductosMasCotizados(Long proveedorId);

    Map<String, Long> obtenerProductosMasCotizadosDeTodosLosProveedores();

    List<Cotizacion> obtenerTodas();
}

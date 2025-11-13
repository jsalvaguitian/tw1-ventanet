package com.tallerwebi.dominio.servicios;

import java.util.List;

import com.tallerwebi.dominio.entidades.Licitacion;
import com.tallerwebi.dominio.enums.EstadoLicitacion;
import com.tallerwebi.dominio.excepcion.NoHayLicitacionesExistentes;

public interface ServicioLicitacion {    

    void crear(Licitacion licitacion);

    Licitacion obtenerPorId(Long id);

    void actualizar(Licitacion licitacion);

    void eliminar(Long id);
    List<Licitacion>obtenerLicitacionesPorIdCliente(Long clienteId) throws NoHayLicitacionesExistentes;
    List<Licitacion>obtenerLicitacionesPorIdProveedor(Long proveedorId) throws NoHayLicitacionesExistentes;

    void actualizarEstado(Long licitacionId, EstadoLicitacion estado);
    void actualizarEstadoYPrecioUnitario(Long licitacionId, EstadoLicitacion estado, Double precioUnitario);
}

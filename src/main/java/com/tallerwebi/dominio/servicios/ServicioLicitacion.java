package com.tallerwebi.dominio.servicios;

import java.util.List;

import com.tallerwebi.dominio.entidades.Licitacion;
import com.tallerwebi.dominio.enums.EstadoLicitacion;

public interface ServicioLicitacion {    

    void crear(Licitacion licitacion);

    Licitacion obtenerPorId(Long id);

    void actualizar(Licitacion licitacion);

    void eliminar(Long id);
    List<Licitacion>obtenerLicitacionesPorIdCliente(Long clienteId);

    void actualizarEstado(Long licitacionId, EstadoLicitacion estado);
}

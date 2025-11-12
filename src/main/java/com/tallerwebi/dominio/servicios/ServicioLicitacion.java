package com.tallerwebi.dominio.servicios;

import java.util.List;

import com.tallerwebi.dominio.entidades.Licitacion;

public interface ServicioLicitacion {    

    void crear(Licitacion licitacion);

    Licitacion obtenerPorId(Long id);

    void actualizar(Licitacion licitacion);

    void eliminar(Long id);
}

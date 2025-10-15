package com.tallerwebi.dominio.servicios;

import java.util.List;

import com.tallerwebi.dominio.entidades.Presentacion;

public interface ServicioPresentacion {
    List<Presentacion> obtener();
    void crearPresentacion(Presentacion producto);
    Presentacion obtenerPorId(Long id);
    void actualizar(Presentacion producto);
    void eliminar(Long id);
}


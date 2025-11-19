package com.tallerwebi.dominio.servicios;

import java.util.List;

import com.tallerwebi.dominio.entidades.MedioDePago;

public interface ServicioMedioDePago {
    List<MedioDePago> obtenerTodosLosMedios();
    MedioDePago obtenerPorId(Long id);
}

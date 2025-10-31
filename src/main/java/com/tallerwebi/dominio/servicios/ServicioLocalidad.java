package com.tallerwebi.dominio.servicios;

import java.util.List;
import com.tallerwebi.dominio.entidades.Localidad;

public interface ServicioLocalidad {
    List<Localidad> obtener();

    void crearLocalidad(Localidad localidad);

    Localidad obtenerPorId(Long id);

    void actualizar(Localidad localidad);

    void eliminar(Long id);
}
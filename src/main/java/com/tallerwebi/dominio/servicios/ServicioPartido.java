package com.tallerwebi.dominio.servicios;

import java.util.List;
import com.tallerwebi.dominio.entidades.Partido;

public interface ServicioPartido {
    List<Partido> obtener();

    void crearPartido(Partido partido);

    Partido obtenerPorId(Long id);

    void actualizar(Partido partido);

    void eliminar(Long id);
}
package com.tallerwebi.dominio.servicios;

import java.util.List;

import com.tallerwebi.dominio.entidades.Alto;
import com.tallerwebi.dominio.entidades.Ancho;
import com.tallerwebi.dominio.entidades.Color;
import com.tallerwebi.dominio.entidades.Localidad;
import com.tallerwebi.dominio.entidades.MaterialDePerfil;
import com.tallerwebi.dominio.entidades.Partido;
import com.tallerwebi.dominio.entidades.Provincia;

public interface ServicioTablas {
    List<Color> obtenerColores();
    List<MaterialDePerfil> obtenerMateriales();
    List<Alto> obtenerAltos();
    List<Ancho> obtenerAnchos();
    List<Provincia> obtenerProvincias();
    List<Localidad> obtenerLocalidadesPorProvincia(Long provinciaId);
    List<Partido> obtenerPartidosPorLocalidad(Long localidadId);
}

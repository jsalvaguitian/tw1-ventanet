package com.tallerwebi.dominio.servicios;

import java.util.List;

import com.tallerwebi.dominio.entidades.Alto;
import com.tallerwebi.dominio.entidades.Ancho;
import com.tallerwebi.dominio.entidades.Color;
import com.tallerwebi.dominio.entidades.Localidad;
import com.tallerwebi.dominio.entidades.MaterialDePerfil;
import com.tallerwebi.dominio.entidades.Partido;
import com.tallerwebi.dominio.entidades.Provincia;
import com.tallerwebi.dominio.entidades.TipoDeVidrio;

public interface ServicioTablas {
    List<Color> obtenerColores();
    List<MaterialDePerfil> obtenerMateriales();
    List<Alto> obtenerAltos();
    List<Ancho> obtenerAnchos();
    List<Provincia> obtenerProvincias();
    List<Localidad> obtenerLocalidadesPorProvincia(Long provinciaId);
    List<Partido> obtenerPartidosPorLocalidad(Long localidadId);
    Alto obtenerAltoPorId(Long id);
    Ancho obtenerAnchoPorId(Long id);
    MaterialDePerfil obtenerMaterialPorId(Long id);
    Color obtenerColorPorId(Long id);
    TipoDeVidrio obtenerTipoDeVidrioPorId(Long id);
}

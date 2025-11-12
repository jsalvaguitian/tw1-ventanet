package com.tallerwebi.dominio.servicios;

import java.util.List;

import com.tallerwebi.dominio.entidades.Alto;
import com.tallerwebi.dominio.entidades.Ancho;
import com.tallerwebi.dominio.entidades.Color;
import com.tallerwebi.dominio.entidades.Localidad;
import com.tallerwebi.dominio.entidades.Marca;
import com.tallerwebi.dominio.entidades.Material;
import com.tallerwebi.dominio.entidades.Partido;
import com.tallerwebi.dominio.entidades.Presentacion;
import com.tallerwebi.dominio.entidades.Provincia;
import com.tallerwebi.dominio.entidades.SubTipoProducto;
import com.tallerwebi.dominio.entidades.TipoDeVidrio;
import com.tallerwebi.dominio.entidades.TipoProducto;

public interface ServicioTablas {
    List<Color> obtenerColores();
    List<Material> obtenerMateriales();
    List<Alto> obtenerAltos();
    List<Ancho> obtenerAnchos();
    List<Provincia> obtenerProvincias();
    List<Localidad> obtenerLocalidadesPorProvincia(Long provinciaId);
    List<Partido> obtenerPartidosPorLocalidad(Long localidadId);
    List<Marca> obtenerMarcas();
    List<Presentacion> obtenePresentaciones();
    List<SubTipoProducto> obteneSubTipoProductos();
    List<TipoDeVidrio> obtenerTipoDeVidrios();
    List<TipoProducto> obtenerTipoProductos();
}


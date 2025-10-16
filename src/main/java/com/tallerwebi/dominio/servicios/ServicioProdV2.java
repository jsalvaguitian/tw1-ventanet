package com.tallerwebi.dominio.servicios;

import java.util.List;
import java.util.Set;

import com.tallerwebi.dominio.entidades.Producto;

public interface ServicioProdV2 {

    Set<Producto> obtenerTodosLosProductos();

    List<String> obtenerTodosLosTiposProductos();

}

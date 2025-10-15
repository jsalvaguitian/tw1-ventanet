package com.tallerwebi.dominio.servicios;

import java.util.List;

import com.tallerwebi.dominio.entidades.Marca;


public interface ServicioMarca {
List<Marca> obtener();
    void crearMarca(Marca producto);
    Marca obtenerPorId(Long id);
    void actualizar(Marca producto);
    void eliminar(Long id);    
} 


package com.tallerwebi.dominio.repositorios_interfaces;

import java.util.List;

import com.tallerwebi.dominio.entidades.MedioDePago;

public interface RepositorioMedioDePago {
    List<MedioDePago> obtenerTodos();
    MedioDePago  buscarPorId(Long medioId);
}

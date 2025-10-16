package com.tallerwebi.dominio.repositorios_interfaces;

import java.util.List;

import com.tallerwebi.dominio.entidades.Proveedor;

public interface RepositorioProveedor {
    Proveedor buscarProveedorPorCuit(String cuit);

    List<Proveedor> obtenerTodosLosNombresProveedoresActivos();
    
}

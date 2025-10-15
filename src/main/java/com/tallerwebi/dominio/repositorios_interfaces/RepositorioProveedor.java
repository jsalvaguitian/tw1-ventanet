package com.tallerwebi.dominio.repositorios_interfaces;

import com.tallerwebi.dominio.entidades.Proveedor;

public interface RepositorioProveedor {
    Proveedor buscarProveedorPorCuit(String cuit);
    
}

package com.tallerwebi.dominio.servicios;

import java.util.List;

import com.tallerwebi.dominio.entidades.Proveedor;
 
public interface ServicioProveedorI {
    List<Proveedor> obtenerTodosLosProveedoresActivos();
    Proveedor obtenerPorIdUsuario(Long idUsuario); 
}

package com.tallerwebi.dominio.servicios;

import java.util.List;

import com.tallerwebi.dominio.entidades.Proveedor;
 
public interface ServicioProveedorI {

    List<Proveedor> obtenerTodosLosProveedoresActivos();
    List<Proveedor> obtenerTodosLosProveedoresPendientes();
    Proveedor buscarPorId(Long id);
    void actualizar(Proveedor proveedor);

    Proveedor obtenerPorIdUsuario(Long idUsuario); 
}

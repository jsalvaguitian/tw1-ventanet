package com.tallerwebi.dominio.repositorios_interfaces;

import java.util.List;

import com.tallerwebi.dominio.entidades.Proveedor;
import com.tallerwebi.dominio.enums.EstadoUsuario;

public interface RepositorioProveedor {
    Proveedor buscarProveedorPorCuit(String cuit);

    List<Proveedor> obtenerTodosLosNombresProveedoresActivos();

    List<Proveedor> obtenerTodosLosProveedoresPendientes();

    Proveedor buscarPorId(Long id);

    void actualizar(Proveedor proveedor);

    Proveedor buscarProveedorPorIdUsuario(Long idUsuario);

    Integer contarProveedores();

    Integer contarProveedores(EstadoUsuario estado);
    
}

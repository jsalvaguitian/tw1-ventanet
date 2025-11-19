package com.tallerwebi.dominio.repositorios_interfaces;

import java.util.List;

import com.tallerwebi.dominio.entidades.Proveedor;
import com.tallerwebi.dominio.enums.EstadoUsuario;
import com.tallerwebi.dominio.enums.Rubro;

public interface RepositorioProveedor {
    Proveedor buscarProveedorPorCuit(String cuit);

    List<Proveedor> obtenerTodosLosNombresProveedoresActivos();

    List<Proveedor> obtenerTodosLosProveedoresPendientes();

    Proveedor buscarPorId(Long id);

    void actualizar(Proveedor proveedor);

    Proveedor buscarProveedorPorIdUsuario(Long idUsuario);

    Integer contarProveedores();

    Integer contarProveedores(EstadoUsuario estado);

    List<Rubro> obtenerRubrosActivos();

    List<Proveedor> listarPorRubro(Rubro rubro);
    List<Proveedor> listarTodosPorEstado(Boolean activo);
    Proveedor obtenerProveedorConMedios(Long id);
}

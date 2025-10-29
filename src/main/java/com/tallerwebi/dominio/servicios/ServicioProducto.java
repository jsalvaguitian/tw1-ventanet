package com.tallerwebi.dominio.servicios;

import java.util.List;

import com.tallerwebi.dominio.entidades.Producto;
import com.tallerwebi.dominio.excepcion.ProductoExistente;

public interface ServicioProducto {
    List<Producto> obtener();
    void crearProducto(Producto producto)throws ProductoExistente;
    Producto obtenerPorId(Long id);
    void actualizar(Producto producto);
    void eliminar(Long id);
    List<Producto> buscarConFiltros(Long tipoProductoId);
    List<Producto> buscarPorProveedorId(Long proveedorId);
}

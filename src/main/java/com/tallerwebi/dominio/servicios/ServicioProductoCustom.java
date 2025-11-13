package com.tallerwebi.dominio.servicios;

import java.util.List;
import com.tallerwebi.dominio.entidades.ProductoCustom;
import com.tallerwebi.dominio.excepcion.NoHayProductoCustomExistente;

public interface ServicioProductoCustom {
    List<ProductoCustom> obtener();

    void crearProducto(ProductoCustom producto);

    ProductoCustom obtenerPorId(Long id) throws NoHayProductoCustomExistente;

    void actualizar(ProductoCustom producto);

    void eliminar(Long id) throws NoHayProductoCustomExistente;
}

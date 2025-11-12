package com.tallerwebi.dominio.servicios;

import java.util.List;

import com.tallerwebi.dominio.entidades.Producto;
import com.tallerwebi.dominio.entidades.TipoProducto;
import com.tallerwebi.dominio.entidades.SubTipoProducto;
import com.tallerwebi.dominio.excepcion.ProductoExistente;
import com.tallerwebi.presentacion.dto.ProductoDTO;
import com.tallerwebi.presentacion.dto.ProductoGenericoDTO;

public interface ServicioProducto {
    List<Producto> obtener();

    void crearProducto(Producto producto) throws ProductoExistente;

    Producto obtenerPorId(Long id);

    void actualizar(Producto producto);

    void eliminar(Long id);

    List<Producto> buscarConFiltros(Long tipoProductoId);

    List<Producto> buscarPorProveedorId(Long proveedorId);
    List<Producto> obtenerProductosFiltrados(Long idProveedor, String busqueda, Long tipoProductoId,
            Long tipoVentanaId);
    List<TipoProducto> obtenerTiposProductos(Long idProveedor);
    List<SubTipoProducto> obtenerTiposVentanas(Long idProveedor);
    List<ProductoGenericoDTO> obtenerProductosGenericos();

    List<Producto> buscarProductosParaCotizacion(
            Long tipoProductoId,
            Long tipoVentanaId,
            Long anchoId,
            Long altoId,
            Long materialId,
            Long vidrioId,
            Long colorId,
            Boolean premarco,
            Boolean barrotillos);

    List<Producto> obtenertodosPorListadoId(List<Long> productosIds);

    List<Producto> filtrarProductos(Long tipoProductoId, Long tipoVentanaId, Long anchoId, Long altoId,
            Long materialPerfilId, Long colorId);
}

package com.tallerwebi.dominio.servicios;

import java.util.List;

import javax.transaction.Transactional;
import org.springframework.stereotype.Service;

import com.tallerwebi.dominio.entidades.Producto;
import com.tallerwebi.dominio.entidades.TipoProducto;
import com.tallerwebi.dominio.entidades.TipoVentana;
import com.tallerwebi.dominio.excepcion.NoHayProductoExistente;
import com.tallerwebi.dominio.excepcion.ProductoExistente;
import com.tallerwebi.infraestructura.RepositorioProductoImpl;
import com.tallerwebi.presentacion.dto.ProductoGenericoDTO;

@Service("servicioProducto")
@Transactional
public class ServicioProductoImpl implements ServicioProducto {
    private final RepositorioProductoImpl productoRepository;

    public ServicioProductoImpl(RepositorioProductoImpl productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    public List<Producto> obtener() {
        return this.productoRepository.obtener();
    }

    @Override
    public void crearProducto(Producto producto) throws ProductoExistente {
        Producto productoEncontrado = productoRepository.obtenerPorNombreMarcaYProveedor(producto.getNombre(),
                producto.getMarca().getId(), producto.getProveedor().getId());

        if (productoEncontrado != null) {
            throw new ProductoExistente();
        }
        this.productoRepository.guardar(producto);
    }

    @Override
    public Producto obtenerPorId(Long id) {
        Producto producto = productoRepository.obtener(id);
        if (producto == null) {
            throw new NoHayProductoExistente();
        }
        return producto;
    }

    @Override
    public void actualizar(Producto producto) {

        productoRepository.actualizar(producto);
    }

    @Override
    public void eliminar(Long id) {
        Producto productoObt = productoRepository.obtener(id);
        if (productoObt == null) {
            throw new NoHayProductoExistente();
        }
        productoRepository.eliminar(id);
    }

    @Override
    public List<Producto> buscarConFiltros(Long tipoProductoId) {
        return productoRepository.buscarConFiltros(tipoProductoId);
    }

    @Override
    public List<Producto> buscarPorProveedorId(Long proveedorId) {
        return productoRepository.buscarPorProveedorId(proveedorId);
    }

    @Override
    public List<Producto> obtenerProductosFiltrados(Long idProveedor, String busqueda, Long tipoProductoId,
            Long tipoVentanaId) {

        return productoRepository.filtrarProductos(idProveedor, busqueda, tipoProductoId, tipoVentanaId);
    }

    @Override
    public List<TipoProducto> obtenerTiposProductos(Long idProveedor) {
        return productoRepository.obtenerTiposProductoPorProveedor(idProveedor);
    }

    @Override
    public List<TipoVentana> obtenerTiposVentanas(Long idProveedor) {
        return productoRepository.obtenerTiposVentanasPorProveedor(idProveedor);
    }

    @Override
    public List<ProductoGenericoDTO> obtenerProductosGenericos() {
        return productoRepository.obtenerProductosGenericos();
    }    

    @Override
   public List<Producto> buscarProductosParaCotizacion
    (Long tipoVentanaId,
     Long anchoId,
     Long altoId,
     Long materialId,
     Long vidrioId,
     Long colorId,
     Boolean premarco,
     Boolean barrotillos) {
        return productoRepository.buscarProductosParaCotizacion(         
         tipoVentanaId,
         anchoId,
         altoId,
         materialId,
         vidrioId,
         colorId,
         premarco,
         barrotillos);
    }

    @Override
    public List<Producto> obtenertodosPorListadoId(List<Long> productosIds) {
       return productoRepository.obtenertodosPorListadoId(productosIds);
    }
}

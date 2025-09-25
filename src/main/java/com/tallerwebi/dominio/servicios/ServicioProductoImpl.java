package com.tallerwebi.dominio.servicios;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.tallerwebi.dominio.entidades.Producto;
import com.tallerwebi.dominio.excepcion.NoHayProductoExistente;
import com.tallerwebi.dominio.excepcion.ProductoExistente;
import com.tallerwebi.infraestructura.RepositorioProductoImpl;

@Service("servicioProducto")
@Transactional
public class ServicioProductoImpl implements ServicioProducto {
    private final RepositorioProductoImpl  productoRepository;

    public ServicioProductoImpl(RepositorioProductoImpl productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    public List<Producto> obtener() {
        return this.productoRepository.obtener();
    }

    @Override
    public void crearProducto(Producto producto)throws ProductoExistente {
        Producto productoEncontrado = productoRepository.obtenerPorNombreMarcaYProveedor(producto.getNombre(), producto.getMarcaId(), producto.getProveedorId());
        if(productoEncontrado != null){
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
        Producto productoObt = productoRepository.obtener(producto.getId());
        if (productoObt == null) {
            throw new NoHayProductoExistente();
        }
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

}

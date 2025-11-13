package com.tallerwebi.dominio.servicios;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.tallerwebi.dominio.entidades.ProductoCustom;
import com.tallerwebi.dominio.excepcion.NoHayProductoCustomExistente;
import com.tallerwebi.dominio.excepcion.NoHayProductoExistente;
import com.tallerwebi.infraestructura.RepositorioProductoCustomImpl;

@Service("servicioProductoCustom")
@Transactional
public class ServicioProductoCustomImpl implements ServicioProductoCustom {
    private final RepositorioProductoCustomImpl productoRepository;

    public ServicioProductoCustomImpl(RepositorioProductoCustomImpl productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    public List<ProductoCustom> obtener() {
        return this.productoRepository.obtener();
    }

    @Override
    public void crearProducto(ProductoCustom producto) {        
        this.productoRepository.guardar(producto);
    }

    @Override
    public ProductoCustom obtenerPorId(Long id) throws NoHayProductoExistente {
        ProductoCustom producto = productoRepository.obtener(id);
        if (producto == null) {
            throw new NoHayProductoExistente();
        }
        return producto;
    }

    @Override
    public void actualizar(ProductoCustom producto) {
        productoRepository.actualizar(producto);
    }

    @Override
    public void eliminar(Long id) throws NoHayProductoCustomExistente {
        ProductoCustom productoObt = productoRepository.obtener(id);
        if (productoObt == null) {
            throw new NoHayProductoCustomExistente();
        }
        productoRepository.eliminar(id);
    }

}

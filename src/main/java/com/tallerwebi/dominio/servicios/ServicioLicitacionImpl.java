package com.tallerwebi.dominio.servicios;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.tallerwebi.dominio.entidades.Licitacion;
import com.tallerwebi.dominio.entidades.Producto;
import com.tallerwebi.dominio.entidades.ProductoCustom;
import com.tallerwebi.dominio.enums.EstadoLicitacion;
import com.tallerwebi.dominio.excepcion.NoHayLicitacionesExistentes;
import com.tallerwebi.infraestructura.RepositorioLicitacionImpl;
import com.tallerwebi.infraestructura.RepositorioProductoCustomImpl;

@Service("servicioLicitacion")
@Transactional
public class ServicioLicitacionImpl implements ServicioLicitacion {

    private final RepositorioLicitacionImpl licitacionRepository;
    private final RepositorioProductoCustomImpl productoCustomRepository;

    public ServicioLicitacionImpl(RepositorioLicitacionImpl licitacionRepository,RepositorioProductoCustomImpl productoCustomRepository) {
        this.licitacionRepository = licitacionRepository;
        this.productoCustomRepository = productoCustomRepository;
    }

    

    @Override
    public void crear(Licitacion licitacion) {
        licitacionRepository.guardar(licitacion);
    }

    @Override
    public Licitacion obtenerPorId(Long id) {
        Licitacion licitacion = licitacionRepository.obtenerPorId(id);
        
        return licitacion;
    }

    @Override
    public void actualizar(Licitacion licitacion) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'actualizar'");
    }

    @Override
    public void eliminar(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'eliminar'");
    }

     @Override
    public void actualizarEstado(Long licitacionId, EstadoLicitacion estado){

        Licitacion licitacion = licitacionRepository.obtenerPorId(licitacionId);        

        licitacion.setEstado(estado);
        licitacionRepository.actualizarEstado(licitacion);
    }

     @Override
    public void actualizarEstadoYPrecioUnitario(Long licitacionId, EstadoLicitacion estado, Double precioUnitario){

        Licitacion licitacion = licitacionRepository.obtenerPorId(licitacionId);
        licitacion.setEstado(estado);
        ProductoCustom productoCustom = licitacion.getProductoCustom();
        productoCustom.setPrecio(precioUnitario);
        productoCustomRepository.actualizar(productoCustom);
        licitacion.setProductoCustom(productoCustom);
        licitacionRepository.actualizarEstado(licitacion);
    }



    @Override
    public List<Licitacion> obtenerLicitacionesPorIdCliente(Long clienteId) throws NoHayLicitacionesExistentes {        
        List<Licitacion> licitaciones = licitacionRepository.obtenerPorIdCliente(clienteId);
        
        if (licitaciones == null) {
            throw new NoHayLicitacionesExistentes();
        }
        return licitaciones;
    }



    @Override
    public List<Licitacion> obtenerLicitacionesPorIdProveedor(Long proveedorId) throws NoHayLicitacionesExistentes {
        List<Licitacion> licitaciones = licitacionRepository.obtenerPorIdProveedor(proveedorId);
        
        if (licitaciones == null) {
            throw new NoHayLicitacionesExistentes();
        }
        return licitaciones;
    }

}

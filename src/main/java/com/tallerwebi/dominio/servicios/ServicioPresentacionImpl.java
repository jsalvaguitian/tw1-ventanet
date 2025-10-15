package com.tallerwebi.dominio.servicios;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.tallerwebi.dominio.entidades.Presentacion;
import com.tallerwebi.dominio.excepcion.NoHayProductoExistente;
import com.tallerwebi.infraestructura.RepositorioPresentacionImpl;

@Service("servicioPresentacion")
@Transactional
public class ServicioPresentacionImpl implements ServicioPresentacion {
    private final RepositorioPresentacionImpl  presentacionRepository;    

    public ServicioPresentacionImpl(RepositorioPresentacionImpl presentacionRepository) {
        this.presentacionRepository = presentacionRepository;
    }

     @Override
    public void actualizar(Presentacion producto) {
        Presentacion productoObt = presentacionRepository.obtener(producto.getId());
        if (productoObt == null) {
            throw new NoHayProductoExistente();
        }
        presentacionRepository.actualizar(producto);
    }

    @Override
    public void eliminar(Long id) {
        Presentacion presentacionObt = presentacionRepository.obtener(id);
        if (presentacionObt == null) {
            throw new NoHayProductoExistente();
        }
        presentacionRepository.eliminar(id);
    }

    @Override
    public List<Presentacion> obtener() {
        return this.presentacionRepository.obtener();
        
    }

    @Override
    public void crearPresentacion(Presentacion presentacion) {        
       this.presentacionRepository.guardar(presentacion);   
    }

    @Override
    public Presentacion obtenerPorId(Long id) {
        Presentacion presentacion = presentacionRepository.obtener(id);
        if (presentacion == null) {
            throw new NoHayProductoExistente();
        }
        return presentacion;
    }
    
}


package com.tallerwebi.dominio.servicios;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.tallerwebi.dominio.entidades.Licitacion;
import com.tallerwebi.dominio.enums.EstadoLicitacion;
import com.tallerwebi.dominio.excepcion.NoHayProductoExistente;
import com.tallerwebi.infraestructura.RepositorioLicitacionImpl;

@Service("servicioLicitacion")
@Transactional
public class ServicioLicitacionImpl implements ServicioLicitacion {

    private final RepositorioLicitacionImpl licitacionRepository;

    public ServicioLicitacionImpl(RepositorioLicitacionImpl licitacionRepository) {
        this.licitacionRepository = licitacionRepository;
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
    public List<Licitacion> obtenerLicitacionesPorIdCliente(Long clienteId) {        
        List<Licitacion> licitaciones = licitacionRepository.obtenerPorIdCliente(clienteId);
        
        if (licitaciones == null || licitaciones.isEmpty()) {
            throw new NoHayProductoExistente();
        }
        return licitaciones;
    }

}

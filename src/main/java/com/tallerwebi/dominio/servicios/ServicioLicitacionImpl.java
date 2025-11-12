package com.tallerwebi.dominio.servicios;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.tallerwebi.dominio.entidades.Cotizacion;
import com.tallerwebi.dominio.entidades.Licitacion;
import com.tallerwebi.infraestructura.RepositorioLicitacionImpl;
import com.tallerwebi.infraestructura.RepositorioLocalidadImpl;

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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'obtenerPorId'");
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

}

package com.tallerwebi.dominio.servicios;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.tallerwebi.dominio.entidades.Cotizacion;
import com.tallerwebi.dominio.excepcion.CotizacionesExistente;
import com.tallerwebi.dominio.excepcion.NoHayProductoExistente;
import com.tallerwebi.infraestructura.RepositorioCotizacionImpl;

@Service("servicioCotizacion")
public class ServicioCotizacionImpl implements ServicioCotizacion {
    private final RepositorioCotizacionImpl cotizacionRepository;

    public ServicioCotizacionImpl(RepositorioCotizacionImpl cotizacionRepository) {
        this.cotizacionRepository = cotizacionRepository;
    }

    @Override
    @Transactional
    public List<Cotizacion> obtenerCotizacionPorIdCliente(Long id) {
        List<Cotizacion> cotizaciones = cotizacionRepository.obtenerPorIdCliente(id);
        if (cotizaciones == null || cotizaciones.isEmpty()) {
            throw new NoHayProductoExistente();
        }
        return cotizaciones;
    }
    

    @Override
    @Transactional
    public void crearCotizacion(Cotizacion cotizacion) throws CotizacionesExistente {
        this.cotizacionRepository.guardar(cotizacion);
    }

    @Override
    @Transactional
    public List<Cotizacion> obtener() {
        return this.cotizacionRepository.obtener();
    }

}

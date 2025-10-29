package com.tallerwebi.dominio.servicios;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;
import com.tallerwebi.dominio.entidades.Cotizacion;
import com.tallerwebi.dominio.excepcion.NoHayProductoExistente;
import com.tallerwebi.infraestructura.RepositorioCotizacionImpl;

@Service
@Transactional
public class ServicioCotizacionImpl implements ServicioCotizacion {
     private final RepositorioCotizacionImpl cotizacionRepository;

    public ServicioCotizacionImpl(RepositorioCotizacionImpl cotizacionRepository) {
        this.cotizacionRepository = cotizacionRepository;
    }

    @Override
    public Cotizacion obtenerPorId(Long id) {
        Cotizacion cotizacion = cotizacionRepository.obtenerPorId(id);
        if (cotizacion == null) {
            throw new NoHayProductoExistente();
        }
        return cotizacion;
    }

    @Override
    public List<Cotizacion> obtenerPorIdProveedor(Long proveedorId) {
        return cotizacionRepository.obtenerPorIdProveedor(proveedorId);
    }

    @Override
    public void actualizarEstado(Long estadoId, Long cotizacionId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'actualizarEstado'");
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

}

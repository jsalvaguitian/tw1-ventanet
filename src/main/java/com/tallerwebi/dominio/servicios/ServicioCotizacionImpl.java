package com.tallerwebi.dominio.servicios;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;
import com.tallerwebi.dominio.entidades.Cotizacion;
import com.tallerwebi.dominio.enums.EstadoCotizacion;
import com.tallerwebi.dominio.excepcion.CotizacionesExistente;
import com.tallerwebi.dominio.excepcion.NoHayProductoExistente;
import com.tallerwebi.infraestructura.RepositorioCotizacionImpl;


@Service
@Transactional
public class ServicioCotizacionImpl implements ServicioCotizacion {
     private final RepositorioCotizacionImpl cotizacionRepository;
     private ServicioEmail servicioEmail;

    public ServicioCotizacionImpl(RepositorioCotizacionImpl cotizacionRepository,ServicioEmail servicioEmail) {
        this.cotizacionRepository = cotizacionRepository;
        this.servicioEmail = servicioEmail;
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
    public void actualizarEstado(Long cotizacionId, EstadoCotizacion estado) throws CotizacionesExistente {
        
        Cotizacion cotizacion = cotizacionRepository.obtenerPorId(cotizacionId);
        if (cotizacion == null) {
            throw new CotizacionesExistente();
        }
        
        cotizacion.setEstado(estado);
        cotizacionRepository.actualizarEstado(cotizacion);        
    }

    @Override
    @Transactional
    public List<Cotizacion> obtenerCotizacionPorIdCliente(Long id) {
        System.out.println("[ServicioCotizacion] buscar cotizaciones para clienteId=" + id);
        List<Cotizacion> cotizaciones = cotizacionRepository.obtenerPorIdCliente(id);
        System.out.println("[ServicioCotizacion] cantidad encontrada=" + (cotizaciones == null ? 0 : cotizaciones.size()));
        if (cotizaciones == null || cotizaciones.isEmpty()) {
            throw new NoHayProductoExistente();
        }
        return cotizaciones;
    }

    @Override
    public Cotizacion guardar(Cotizacion cotizacion) {
        Cotizacion newCotizacion = cotizacionRepository.guardar(cotizacion);
        
        String asunto = "VENTANET - Nueva Cotización Creada";  
        String cuerpoProveedor = "Se ha creado una nueva cotización con ID: " + newCotizacion.getId() +
                        "\nMonto Total: " + newCotizacion.getMontoTotal() +
                        "\nEstado: " + newCotizacion.getEstado().name() +
                        "\nCliente: " + newCotizacion.getCliente().getNombre() + 
                        "\nEmail Cliente: " + newCotizacion.getCliente().getEmail();                                                    
        
        //servicioEmail.enviarEmail(newCotizacion.getProveedor().getEmail(), asunto, cuerpoProveedor, false);

        String cuerpoCliente = "Se ha creado una nueva cotización con ID: " + newCotizacion.getId() +
                        "\nMonto Total: " + newCotizacion.getMontoTotal() +
                        "\nEstado: " + newCotizacion.getEstado().name() +
                        "\nProveedor: " + newCotizacion.getProveedor().getRazonSocial() + 
                        "\nEmail Proveedor: " + newCotizacion.getProveedor().getEmail();
        //servicioEmail.enviarEmail(newCotizacion.getCliente().getEmail(), asunto, cuerpoCliente, false); 
        return newCotizacion;
    }   

}

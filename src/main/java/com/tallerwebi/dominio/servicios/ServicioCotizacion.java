package com.tallerwebi.dominio.servicios;
import java.util.List;
import com.tallerwebi.dominio.entidades.Cotizacion;
import com.tallerwebi.dominio.excepcion.CotizacionesExistente;

public interface ServicioCotizacion {
    List<Cotizacion> obtener();
    void crearCotizacion(Cotizacion cotizacion)throws CotizacionesExistente;
    List<Cotizacion> obtenerCotizacionPorIdCliente(Long id);
}

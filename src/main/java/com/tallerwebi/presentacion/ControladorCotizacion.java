package com.tallerwebi.presentacion;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;
import com.tallerwebi.dominio.entidades.Cotizacion;
import com.tallerwebi.dominio.excepcion.NoHayProductoExistente;
import com.tallerwebi.dominio.servicios.ServicioCotizacion;


@Controller
@RequestMapping("/clientes")
public class ControladorCotizacion {
    /*private ServicioCotizacion servicioCotizacion;

    public ControladorCotizacion(ServicioCotizacion servicioCotizacion) {
        new ArrayList<>();
        this.servicioCotizacion = servicioCotizacion;
    }
    @RequestMapping(path = "/dashboards", method = RequestMethod.GET)
    public ModelAndView mostrarCotizaciones() {

        ModelMap modelo = new ModelMap();

        try {
            List<Cotizacion> cotizaciones = this.servicioCotizacion.obtenerCotizacionPorIdCliente((long) 2);
            modelo.put("cotizaciones", cotizaciones);
            if (cotizaciones.isEmpty()) {
                modelo.put("exito", "No hay Cotizaciones");
            } else {
                modelo.put("exito", "Hay Cotizaciones.");
            }
        } catch (NoHayProductoExistente e) {
            modelo.put("cotizaciones", new ArrayList<>());
            modelo.put("error", "no hay cotizaciones");
        }

        return new ModelAndView("dashboard", modelo);
    }*/
}

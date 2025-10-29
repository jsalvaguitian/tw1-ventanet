package com.tallerwebi.presentacion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tallerwebi.dominio.servicios.ServicioCotizacion;

@Controller
@RequestMapping("/cotizacion")
public class ControladorCotizacion {
    private final ServicioCotizacion servicioCotizacion;

    @Autowired
    public ControladorCotizacion(ServicioCotizacion servicioCotizacion) {
        this.servicioCotizacion = servicioCotizacion;        
    }

}

package com.tallerwebi.presentacion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.tallerwebi.dominio.entidades.Cotizacion;
import com.tallerwebi.dominio.enums.EstadoCotizacion;
import com.tallerwebi.dominio.excepcion.CotizacionesExistente;
import com.tallerwebi.dominio.servicios.ServicioCotizacion;

@Controller
@RequestMapping("/cotizacion")
public class ControladorCotizacion {
    private final ServicioCotizacion servicioCotizacion;

    @Autowired
    public ControladorCotizacion(ServicioCotizacion servicioCotizacion) {
        this.servicioCotizacion = servicioCotizacion;
    }

    @GetMapping("/detalle/{id}")
    @ResponseBody
    public Cotizacion obtenerDetalleCotizacion(@PathVariable Long id) {
        return servicioCotizacion.obtenerPorId(id);
    }

    @PostMapping("/{id}/cambiar-estado/{nuevoEstado}")
    @ResponseBody
    public ModelAndView cambiarEstadoCotizacion(@PathVariable Long id, @PathVariable String nuevoEstado) throws CotizacionesExistente {
        
        if(nuevoEstado == null || nuevoEstado.isEmpty()) {
            return new ModelAndView("redirect:/proveedor/dashboard-proveedor");
        }
        EstadoCotizacion estado = EstadoCotizacion.valueOf(nuevoEstado);          
        servicioCotizacion.actualizarEstado(id, estado);        
        return new ModelAndView("redirect:/proveedor/dashboard-proveedor");
    }

}

package com.tallerwebi.presentacion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.tallerwebi.dominio.entidades.Cotizacion;
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
    public ResponseEntity<?> cambiarEstadoCotizacion(@PathVariable Long id, @PathVariable String nuevoEstado) {
        // Lógica para actualizar el estado en el servicio/repositorio
        servicioCotizacion.actualizarEstado(id, 1L);
        return ResponseEntity.ok().build();
    }

}

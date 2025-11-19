/*package com.tallerwebi.presentacion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.tallerwebi.dominio.servicios.ServicioEstadisticas;

@Controller
public class ControladorHome {
    @Autowired
    private ServicioEstadisticas servicioEstadisticas;

    @GetMapping("/")
    public String home(Model model) {

        model.addAttribute("topProductos",
            servicioEstadisticas.obtenerTopProductos(5)
        );

        return "home";
    }
}
*/
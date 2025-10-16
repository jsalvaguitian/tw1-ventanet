package com.tallerwebi.presentacion;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ControladorPresupuesto {

    @GetMapping("/presupuesto")
    public String mostrarFormulario(Model model) {
        return "presupuesto";
    }

    @PostMapping("/presupuesto")
    public String procesarFormulario(
            @RequestParam String tipoVentana,
            @RequestParam Double ancho,
            @RequestParam Double alto,
            @RequestParam String material,
            @RequestParam String vidrio,
            @RequestParam String color,
            @RequestParam(required = false) boolean premarco,
            @RequestParam(required = false) boolean barrotillos,
            Model model) {

        model.addAttribute("mensaje", "Tu solicitud fue enviada!");
        return "home";
    }
}


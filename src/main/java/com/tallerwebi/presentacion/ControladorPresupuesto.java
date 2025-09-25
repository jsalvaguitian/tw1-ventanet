package com.ventanet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class PresupuestoController {

    @GetMapping("/presupuesto")
    public String mostrarFormulario(Model model) {
        model.addAttribute("tiposVentana", new String[]{"Corrediza", "Oscilobatiente", "Paño fijo", "Abatible", "Plegable"});
        model.addAttribute("materiales", new String[]{"Aluminio", "PVC", "Madera"});
        model.addAttribute("vidrios", new String[]{"Simple", "Doble (DVH)", "Laminado", "Templado"});
        model.addAttribute("colores", new String[]{"Blanco", "Gris", "Marrón", "Negro"});
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
            Model model
    ) {
        model.addAttribute("mensaje", "Tu solicitud de presupuesto ha sido enviada con éxito ✅");
        model.addAttribute("tipoVentana", tipoVentana);
        model.addAttribute("ancho", ancho);
        model.addAttribute("alto", alto);
        model.addAttribute("material", material);
        model.addAttribute("vidrio", vidrio);
        model.addAttribute("color", color);
        model.addAttribute("premarco", premarco);
        model.addAttribute("barrotillos", barrotillos);

        return "confirmacion"; 
    }
}

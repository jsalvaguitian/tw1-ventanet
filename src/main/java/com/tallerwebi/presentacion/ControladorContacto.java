package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.servicios.ServicioEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorContacto {

    private ServicioEmail servicioEmail;

    @Autowired
    public ControladorContacto(ServicioEmail servicioEmail) {
        this.servicioEmail = servicioEmail;
    }

    @PostMapping("/contacto/enviar")
    public ModelAndView enviarMensajeContacto(@RequestParam("nombre") String nombre, @RequestParam("email") String emailUsuario, @RequestParam("mensaje") String mensaje) {
        try {
            // 1. Definimos el destinatario (tu cuenta de prueba)
            String destinatario = "sitiodeventas.ventanet@gmail.com";
            
            String asunto = "Ventanet - Mensaje de Contacto: " + nombre;

            String cuerpoEmail = "Has recibido un nuevo mensaje desde el formulario de contacto.\n\n" +
                                 "Datos del usuario:\n" +
                                 "------------------\n" +
                                 "Nombre: " + nombre + "\n" +
                                 "Email: " + emailUsuario + "\n\n" +
                                 "Mensaje:\n" +
                                 mensaje;

            servicioEmail.enviarEmail(destinatario, asunto, cuerpoEmail, false);

            return new ModelAndView("redirect:/home?contacto=true#contacto");

        } catch (Exception e) {
            return new ModelAndView("redirect:/home?error=true#contacto");
        }
    }
}
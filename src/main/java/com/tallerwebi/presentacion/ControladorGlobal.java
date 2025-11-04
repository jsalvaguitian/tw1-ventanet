package com.tallerwebi.presentacion;

import com.tallerwebi.presentacion.dto.UsuarioSesionDto;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ControladorGlobal {

    // Esto agrega 'usuarioLogueado' a todos los modelos automáticamente
    @ModelAttribute("usuarioLogueado")
    public UsuarioSesionDto agregarUsuarioALaVista(HttpServletRequest request) {
        return (UsuarioSesionDto) request.getSession().getAttribute("usuarioLogueado");
    }
}
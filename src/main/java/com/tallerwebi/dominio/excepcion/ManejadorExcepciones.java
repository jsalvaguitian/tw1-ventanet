package com.tallerwebi.dominio.excepcion;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;

import com.tallerwebi.dominio.excepcion.NoHayProductoExistente;
import com.tallerwebi.dominio.excepcion.ProveedorNoExistente;
import com.tallerwebi.dominio.excepcion.NoHayCotizacionExistente;
import com.tallerwebi.dominio.excepcion.NoSePudoObtenerDolarException;

@ControllerAdvice
public class ManejadorExcepciones {

    @ExceptionHandler(NoHayProductoExistente.class)
    public String manejarNoHayProductoExistente(NoHayProductoExistente ex, Model model) {
        model.addAttribute("mensaje", "No encontramos el producto que buscabas. Volver al catálogo.");
        return "excepcion";
    }

    @ExceptionHandler(ProveedorNoExistente.class)
    public String manejarProveedorNoExistente(ProveedorNoExistente ex, Model model) {
        model.addAttribute("mensaje", "No encontramos el proveedor que buscabas. Volver al catálogo.");
        return "excepcion";
    }

    @ExceptionHandler(NoHayCotizacionExistente.class)
    public String manejarNoHayCotizacionExistente(NoHayCotizacionExistente ex, Model model, HttpServletRequest request) {
        model.addAttribute("mensaje", ex.getMessage());
        
        Object usuarioSesion = request.getSession().getAttribute("usuarioLogueado");
        model.addAttribute("usuarioSesion", usuarioSesion);
        
        return "excepcion-dashboard";
    }

    @ExceptionHandler(NoSePudoObtenerDolarException.class)
    public String manejarDolarException(NoSePudoObtenerDolarException ex, Model model) {
        model.addAttribute("mensaje", ex.getMessage());
        return null;
    }
}
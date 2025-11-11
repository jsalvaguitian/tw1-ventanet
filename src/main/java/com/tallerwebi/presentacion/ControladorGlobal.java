package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.servicios.ServicioDolar;
import com.tallerwebi.presentacion.dto.UsuarioSesionDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ControladorGlobal {

    // Esto agrega 'usuarioLogueado' a todos los modelos automáticamente
    @ModelAttribute("usuarioLogueado")
    public UsuarioSesionDto agregarUsuarioALaVista(HttpServletRequest request) {
        return (UsuarioSesionDto) request.getSession().getAttribute("usuarioLogueado");
    }

    @Autowired
    private ServicioDolar servicioDolar;

    @ModelAttribute("dolarData")
    public Map<String, Object> agregarDolarALaVista() {
        Map<String, Object> data = new HashMap<>();
        // lanza excepcion si no hay datos
        data.put("oficial", servicioDolar.getDatosOficialOrThrow());
        return data;
    }
}
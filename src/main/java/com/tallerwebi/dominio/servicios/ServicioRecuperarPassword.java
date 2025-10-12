package com.tallerwebi.dominio.servicios;

import javax.servlet.http.HttpServletRequest;

import com.tallerwebi.dominio.entidades.Usuario;

public interface ServicioRecuperarPassword {

    void enviarEmailDeRecuperacion(Usuario usuario, HttpServletRequest request);

}

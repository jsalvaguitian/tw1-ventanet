package com.tallerwebi.dominio.servicios;

import com.tallerwebi.dominio.entidades.UsuarioAuth;

public interface ServicioUsuarioI {

    UsuarioAuth autenticar(String emailIngresado, String contraseniaIngresada);

    
}

package com.tallerwebi.dominio.servicios;

import com.tallerwebi.dominio.entidades.UsuarioAuth;
import com.tallerwebi.dominio.excepcion.UsuarioInexistenteException;

public interface ServicioUsuarioI {

    UsuarioAuth autenticar(String emailIngresado, String contraseniaIngresada) throws UsuarioInexistenteException;

    
}

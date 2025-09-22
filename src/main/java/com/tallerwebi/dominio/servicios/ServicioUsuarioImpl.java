package com.tallerwebi.dominio.servicios;

import org.springframework.stereotype.Service;

import com.tallerwebi.dominio.entidades.UsuarioAuth;

@Service
public class ServicioUsuarioImpl implements ServicioUsuarioI{

    @Override
    public UsuarioAuth autenticar(String emailIngresado, String contraseniaIngresada) {
        return null;
    }

}

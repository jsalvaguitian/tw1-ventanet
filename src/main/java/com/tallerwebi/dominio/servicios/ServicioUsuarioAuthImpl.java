package com.tallerwebi.dominio.servicios;

import org.springframework.stereotype.Service;

import com.tallerwebi.dominio.entidades.UsuarioAuth;
import com.tallerwebi.dominio.excepcion.UsuarioInexistenteException;
import com.tallerwebi.dominio.utils.PasswordUtil;

/*
@Service
public class ServicioUsuarioAuthImpl implements ServicioUsuarioAuthI {

    private final RepositorioUsuarioAuthImpl repositorioUsuarioAuthImpl;

    public ServicioUsuarioAuthImpl() {
        this.repositorioUsuarioAuthImpl = new RepositorioUsuarioAuthImpl();
    }

    @Override
    public UsuarioAuth autenticar(String emailIngresado, String contraseniaIngresada) throws UsuarioInexistenteException{
        UsuarioAuth usuarioAuth = repositorioUsuarioAuthImpl.buscarPorMail(emailIngresado);// simulacion de repositorio
        if (usuarioAuth != null && PasswordUtil.verificar(contraseniaIngresada, usuarioAuth.getContrasenia())) {
            return usuarioAuth;
        }

        throw new UsuarioInexistenteException();

    }

}
*/

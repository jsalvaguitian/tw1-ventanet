package com.tallerwebi.dominio.servicios;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tallerwebi.dominio.entidades.ResetearPasswordToken;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.repositorios_interfaces.RepositorioTokenRecuperarPassword;
import com.tallerwebi.dominio.repositorios_interfaces.RepositorioUsuario;
import com.tallerwebi.dominio.utils.PasswordUtil;

@Service
@Transactional
public class ServicioCambiarPasswordImpl implements ServicioCambiarPassword {

    private RepositorioTokenRecuperarPassword repositorioTokenRecuperarPassword;
    private RepositorioUsuario repositorioUsuario;

    @Autowired
    public ServicioCambiarPasswordImpl(RepositorioTokenRecuperarPassword repositorioTokenRecuperarPassword,
            RepositorioUsuario repositorioUsuario) {
        this.repositorioTokenRecuperarPassword = repositorioTokenRecuperarPassword;
        this.repositorioUsuario = repositorioUsuario;
    }

    @Override
    @Transactional
    public boolean cambiarPassword(String nuevaPassword, String token) {
        ResetearPasswordToken resetToken = repositorioTokenRecuperarPassword.buscarPorToken(token);
        System.out.println("DEBUG >> resetToken= " + token);
        if (resetToken == null || resetToken.isUsado()) {
            return false;
        }

        Usuario usuario = resetToken.getUsuario();
        String contraseniaHasheada = "";
        contraseniaHasheada = PasswordUtil.hashear(nuevaPassword);
        usuario.setPassword(contraseniaHasheada);
        repositorioUsuario.modificar(usuario);
        repositorioTokenRecuperarPassword.eliminar(resetToken);
        return true;
    }

}

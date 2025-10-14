package com.tallerwebi.dominio.servicios;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tallerwebi.dominio.entidades.ResetearPasswordToken;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.repositorios_interfaces.RepositorioTokenRecuperarPassword;

import antlr.Token;

@Service
@Transactional
public class ServicioRecuperarPasswordImpl implements ServicioRecuperarPassword {

    private RepositorioTokenRecuperarPassword repositorioTokenRecuperarPassword;
    private ServicioEmail servicioEmail;

    @Autowired
    public ServicioRecuperarPasswordImpl(RepositorioTokenRecuperarPassword repositorioTokenRecupeararPassword,
            ServicioEmail servicioEmail) {
        this.repositorioTokenRecuperarPassword = repositorioTokenRecupeararPassword;
        this.servicioEmail = servicioEmail;
    }

    public static ResetearPasswordToken generarToken(Usuario usuario) {
        ResetearPasswordToken token = new ResetearPasswordToken();

        token.setUsuario(usuario);
        token.setToken(UUID.randomUUID().toString()); // String Random
        token.setExpiracionToken(LocalDateTime.now().plusMinutes(30));
        return token;

    }

    @Override
    @Transactional
    public void enviarEmailDeRecuperacion(Usuario usuario, HttpServletRequest request) {
        ResetearPasswordToken token = generarToken(usuario);
        repositorioTokenRecuperarPassword.guardar(token);

        String url = request.getScheme() + "://" +
                request.getServerName() + ":" +
                request.getServerPort() +
                request.getContextPath() + "/cambiar-password?token=" + token.getToken();

        String asunto = "Recuperacion de contrasenia";
        String cuerpo = "Hola " + usuario.getNombre() +
                ". Para cambiar tu contrasenia, hace click en el siguiente enlace: \n " +
                url + "\n\n" + "Este enlace expirara en 30 min.";
        System.out.println("DEBUG >> Email del usuario en servicio: " + usuario.getEmail());

        servicioEmail.enviarEmail(usuario.getEmail(), asunto, cuerpo);

    }

    @Override
    public ResetearPasswordToken buscarPorToken(String token) {
        ResetearPasswordToken tokenEncontrado = repositorioTokenRecuperarPassword.buscarPorToken(token);
        return tokenEncontrado;
    }

}

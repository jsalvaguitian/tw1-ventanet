package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.tallerwebi.dominio.entidades.Cliente;
import com.tallerwebi.dominio.entidades.ResetearPasswordToken;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.repositorios_interfaces.RepositorioTokenRecuperarPassword;
import com.tallerwebi.dominio.servicios.ServicioEmail;
import com.tallerwebi.dominio.servicios.ServicioRecuperarPasswordImpl;

public class ServicioRecuperarPasswordTest {
    private RepositorioTokenRecuperarPassword repositorioMock;
    private ServicioEmail servicioEmailMock;
    private ServicioRecuperarPasswordImpl servicioRecuperarPasswordMock;

    @BeforeEach
    public void init() {
        repositorioMock = mock(RepositorioTokenRecuperarPassword.class);
        servicioEmailMock = mock(ServicioEmail.class);
        servicioRecuperarPasswordMock = mock(ServicioRecuperarPasswordImpl.class);
        servicioRecuperarPasswordMock = new ServicioRecuperarPasswordImpl(repositorioMock, servicioEmailMock);
    }

    @Test
    public void DeberiaGenerarUnTokenValido() {
        Usuario usuario = new Cliente();
        usuario.setEmail("test@gmail.com");
        usuario.setNombre("test");

        ResetearPasswordToken token = ServicioRecuperarPasswordImpl.generarToken(usuario);

        assertNotNull(token);
        assertThat(token.getUsuario(), is(equalTo(usuario)));
        assertThat(token.getExpiracionToken(), is(greaterThan(LocalDateTime.now())));
    }

    @Test
    public void DeberiaEnviarMailDeRecuperacionConElTokenCorrespondiente() {
        Usuario usuario = new Cliente();
        usuario.setEmail("test@gmail.com");
        usuario.setNombre("test");

        // Mokeo el request
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getScheme()).thenReturn("http");
        when(request.getServerName()).thenReturn("localhost");
        when(request.getServerPort()).thenReturn(8080);
        when(request.getContextPath()).thenReturn("/app");

        servicioRecuperarPasswordMock.enviarEmailDeRecuperacion(usuario, request);

        // Veo que se guarde un token
        verify(repositorioMock).guardar(any(ResetearPasswordToken.class));

        // Veo que envie el mail
        verify(servicioEmailMock).enviarEmail(contains("test@gmail.com"), contains("Recuperacion de contrasenia"),
                contains("localhost:8080/app/cambiar-contrasenia?token="));

    }
}

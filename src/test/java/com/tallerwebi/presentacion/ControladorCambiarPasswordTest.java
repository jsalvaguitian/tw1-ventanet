package com.tallerwebi.presentacion;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

import com.tallerwebi.dominio.entidades.ResetearPasswordToken;
import com.tallerwebi.dominio.servicios.ServicioCambiarPassword;
import com.tallerwebi.dominio.servicios.ServicioRecuperarPassword;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.hamcrest.Matchers.*;

public class ControladorCambiarPasswordTest {
    private ServicioCambiarPassword servicioCambiarPassword;
    private ServicioRecuperarPassword servicioRecuperarPassword;
    private ControladorCambiarPassword controlador;

    @BeforeEach
    public void init() {
        servicioCambiarPassword = mock(ServicioCambiarPassword.class);
        servicioRecuperarPassword = mock(ServicioRecuperarPassword.class);
        controlador = new ControladorCambiarPassword(servicioCambiarPassword, servicioRecuperarPassword);
    }

    @Test
    public void cuandoTokenEsValidoMostrarFormularioCambiarPassword() {
        String token = "ashfkjbsdgkj";
        ResetearPasswordToken resetToken = new ResetearPasswordToken();

        when(servicioRecuperarPassword.buscarPorToken(token)).thenReturn(resetToken);

        ModelAndView modelAndView = controlador.mostrarFormilarioCambiarPassword(token);

        assertThat(modelAndView.getViewName(), equalToIgnoringCase("cambiar-password"));
        assertThat(modelAndView.getModel().get("token"), is(equalTo(token)));
    }

    @Test
    public void cuandoElTokenEsInvalidoMostrarVistaTokenInvalido() {
        String token = "terrible-token";

        when(servicioRecuperarPassword.buscarPorToken(token)).thenReturn(null);

        ModelAndView modelAndView = controlador.mostrarFormilarioCambiarPassword(token);

        assertThat(modelAndView.getViewName(), equalToIgnoringCase("token-invalido"));
    }

    @Test
    public void cuandoElTokenEstaUsadoMostrarVistaTokenInvalido() {
        String token = "tokenUsado";
        ResetearPasswordToken tokenUsado = new ResetearPasswordToken();
        tokenUsado.setUsado(true);

        when(servicioRecuperarPassword.buscarPorToken(token)).thenReturn(tokenUsado);

        ModelAndView modelAndView = controlador.mostrarFormilarioCambiarPassword(token);

        assertThat(modelAndView.getViewName(), equalToIgnoringCase("token-invalido"));
    }

}

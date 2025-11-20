package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

import com.tallerwebi.dominio.excepcion.NoHayCotizacionExistente;
import com.tallerwebi.dominio.excepcion.UsuarioInexistenteException;
import com.tallerwebi.dominio.servicios.ServicioClienteI;
import com.tallerwebi.dominio.servicios.ServicioCotizacion;
import com.tallerwebi.presentacion.dto.UsuarioSesionDto;

public class ControladorClienteTest {
    private ControladorCliente controladorCliente;
    private ServicioClienteI servicioClienteI;
    private HttpServletRequest requestMock;
    private HttpSession sessionMock;
    private ServicioCotizacion servicioCotizacion;

    @BeforeEach
    public void init() {
        servicioClienteI = mock(ServicioClienteI.class);
        servicioCotizacion = mock(ServicioCotizacion.class);
        controladorCliente = new ControladorCliente(servicioClienteI, null, servicioCotizacion);
        requestMock = mock(HttpServletRequest.class);
        sessionMock = mock(HttpSession.class);
    }

    // @Test
    // public void queSeMuestreElDashboardDelClienteCuandoSeLoguea() {
    // String vistaEsperada = "dashboard";
    // when(requestMock.getSession()).thenReturn(sessionMock);
    // UsuarioSesionDto uSesionDto = new UsuarioSesionDto();
    // uSesionDto.setRol("CLIENTE");
    // uSesionDto.setUsername("cliente@unlam.edu.ar");

    // when(sessionMock.getAttribute("usuarioLogueado")).thenReturn(uSesionDto);
    // ModelAndView modelAndView = controladorCliente.irDashboard(requestMock);
    // String vistaObtenida = modelAndView.getViewName();

    // assertThat(vistaObtenida, equalTo(vistaEsperada));
    // assertThat(modelAndView.getModel().get("mailCliente"),
    // equalTo(uSesionDto.getUsername()));
    // }

    @Test
    public void queRedirijaAlLoginSiNoEstaLogueado() throws NoHayCotizacionExistente, UsuarioInexistenteException {
        when(requestMock.getSession()).thenReturn(sessionMock);
        when(sessionMock.getAttribute("usuarioLogueado")).thenReturn(null);
        ModelAndView modelAndView = controladorCliente.irDashboard(requestMock);
        assertThat(modelAndView.getViewName(), equalTo("redirect:/login"));
    }

    @Test
    public void queRedirijaAlLoginSiElRolNoEsCliente() throws NoHayCotizacionExistente, UsuarioInexistenteException {
        when(requestMock.getSession()).thenReturn(sessionMock);
        UsuarioSesionDto uSesionDto = new UsuarioSesionDto();
        uSesionDto.setRol("PROVEEDOR");
        uSesionDto.setUsername("proveedor@unlam.edu.ar");
        when(sessionMock.getAttribute("usuarioLogueado")).thenReturn(uSesionDto);
        ModelAndView modelAndView = controladorCliente.irDashboard(requestMock);
        assertThat(modelAndView.getViewName(), equalTo("redirect:/login"));
    }

    // @Test
    // public void queRedirijaAlLoginSiElEmailEsNulo() {
    // when(requestMock.getSession()).thenReturn(sessionMock);
    // UsuarioSesionDto uSesionDto = new UsuarioSesionDto();
    // uSesionDto.setRol("CLIENTE");
    // uSesionDto.setUsername(null);
    // when(sessionMock.getAttribute("usuarioLogueado")).thenReturn(uSesionDto);
    // ModelAndView modelAndView = controladorCliente.irDashboard(requestMock);
    // assertThat(modelAndView.getModel().get("mailCliente"), equalTo(null));
    // assertThat(modelAndView.getViewName(), equalTo("dashboard"));
    // }

}

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

import com.tallerwebi.dominio.excepcion.UsuarioInexistenteException;
import com.tallerwebi.dominio.servicios.ServicioCotizacion;
import com.tallerwebi.dominio.servicios.ServicioProveedorI;
import com.tallerwebi.presentacion.dto.UsuarioSesionDto;

public class ControladorProveedorTest {

    private ControladorProveedor controladorProveedor;
    private ServicioProveedorI servicioProveedorI;
    private ServicioCotizacion servicioCotizacion;
    private HttpServletRequest requestMock;
    private HttpSession sessionMock;

    @BeforeEach
    public void init() {
        servicioProveedorI = mock(ServicioProveedorI.class);
        servicioCotizacion = mock(ServicioCotizacion.class);
        controladorProveedor = new ControladorProveedor(servicioProveedorI, servicioCotizacion);
        requestMock = mock(HttpServletRequest.class);
        sessionMock = mock(HttpSession.class);
    }

    @Test
    public void queSeMuestreElDashboardDelProveedorCuandoSeLoguea() throws UsuarioInexistenteException {
        String vistaEsperada = "dashboard-proveedor";

        // Simular request que tiene sesion
        when(requestMock.getSession()).thenReturn(sessionMock);

        // creo un usuario proveedor
        UsuarioSesionDto uSesionDto = new UsuarioSesionDto();
        uSesionDto.setRol("Proveedor");
        uSesionDto.setUsername("proveedor@empresa.com");

        // tengo q simular sesion con usuario logueado
        when(sessionMock.getAttribute("usuarioLogueado")).thenReturn(uSesionDto);
        /**************************** */

        // Ejecucion

        ModelAndView modelAndView = controladorProveedor.irDashboard(requestMock);
        String vistaObtenida = modelAndView.getViewName();

        assertThat(vistaObtenida, equalTo(vistaEsperada));
        assertThat(modelAndView.getModel().get("mailProveedor"), equalTo(uSesionDto.getUsername()));
    }

}

package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

import com.tallerwebi.dominio.servicios.ServicioAdministrador;
import com.tallerwebi.dominio.servicios.ServicioEmail;
import com.tallerwebi.dominio.servicios.ServicioProveedorI;
import com.tallerwebi.presentacion.dto.UsuarioSesionDto;

public class ControladorAdministradorTest {

    private ControladorAdministrador controladorAdmin;
    private ServicioAdministrador servicioAdmin;
    private ServicioProveedorI servicioProveedor;
    private ServicioEmail servicioEmail;
    private HttpServletRequest requestMock;
    private HttpSession sessionMock;
    private ServletContext servletContext;

    @BeforeEach
    public void setUp(){
        servicioAdmin = mock(ServicioAdministrador.class);
        servicioProveedor = mock(ServicioProveedorI.class);
        servicioEmail = mock(ServicioEmail.class);
        servletContext = mock(ServletContext.class);

        controladorAdmin = new ControladorAdministrador(servicioAdmin, servicioProveedor, servicioEmail, servletContext);
        requestMock = mock(HttpServletRequest.class);
        sessionMock = mock(HttpSession.class);
    }



    @Test
    public void queSeMuestreElDashboardDelAdminCuandoSeLoguea() {
        //******* PREPARACION
        String vistaEsperada = "dashboard-admin";
        
        //Simular que el request tenga una sesion
        when(requestMock.getSession()).thenReturn(sessionMock);

        //creo mi admin logueado
        UsuarioSesionDto usuarioSesionDto = new UsuarioSesionDto();
        usuarioSesionDto.setRol("Admin");
        usuarioSesionDto.setUsername("admin@mail.com");

        //simular una sesion con usuario rol admin logueado
        when(sessionMock.getAttribute("usuarioLogueado")).thenReturn(usuarioSesionDto);

        //******* EJECUCION
        ModelAndView modelAndView = controladorAdmin.mostrarDashboard(requestMock);
        String vistaObtenida =modelAndView.getViewName();

        //******* VALIDACION
        assertThat(vistaObtenida, equalTo(vistaEsperada));
        assertThat(modelAndView.getModel().get("mailAdmin"), equalTo(usuarioSesionDto.getUsername()));

    }

}

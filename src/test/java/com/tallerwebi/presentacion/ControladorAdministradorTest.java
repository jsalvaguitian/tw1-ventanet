package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

import com.tallerwebi.dominio.entidades.Cliente;
import com.tallerwebi.dominio.entidades.Proveedor;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.enums.EstadoUsuario;
import com.tallerwebi.dominio.servicios.ServicioAdministrador;
import com.tallerwebi.dominio.servicios.ServicioClienteI;
import com.tallerwebi.dominio.servicios.ServicioEmail;
import com.tallerwebi.dominio.servicios.ServicioProveedorI;
import com.tallerwebi.dominio.servicios.ServicioUsuario;
import com.tallerwebi.dominio.servicios.ServicioEstadisticas;
import com.tallerwebi.dominio.servicios.ServicioCotizacion;
import com.tallerwebi.presentacion.dto.UsuarioSesionDto;

public class ControladorAdministradorTest {

    private ControladorAdministrador controladorAdmin;
    private ServicioAdministrador servicioAdmin;
    private ServicioProveedorI servicioProveedor;
    private ServicioUsuario servicioUsuario;
    private ServicioEmail servicioEmail;
    private HttpServletRequest requestMock;
    private HttpSession sessionMock;
    private ServletContext servletContext;
    private ServicioClienteI servicioCliente;
    private ServicioEstadisticas servicioEstadisticas;
    private ServicioCotizacion servicioCotizacion;

    @BeforeEach
    public void setUp() {
        servicioAdmin = mock(ServicioAdministrador.class);
        servicioProveedor = mock(ServicioProveedorI.class);
        servicioEmail = mock(ServicioEmail.class);
        servletContext = mock(ServletContext.class);
        servicioCliente = mock(ServicioClienteI.class);
        servicioUsuario = mock(ServicioUsuario.class);
        servicioEstadisticas = mock(ServicioEstadisticas.class);
        servicioCotizacion = mock(ServicioCotizacion.class);

        controladorAdmin = new ControladorAdministrador(servicioAdmin, servicioProveedor, servicioEmail,servletContext, servicioCliente, servicioUsuario, servicioEstadisticas, servicioCotizacion);
        requestMock = mock(HttpServletRequest.class);
        sessionMock = mock(HttpSession.class);
    }

    @Test
    public void queSeMuestreElDashboardDelAdminCuandoSeLoguea() {
        // ******* PREPARACION
        String vistaEsperada = "dashboard-admin";

        // Simular que el request tenga una sesion
        when(requestMock.getSession()).thenReturn(sessionMock);

        // creo mi admin logueado
        UsuarioSesionDto usuarioSesionDto = new UsuarioSesionDto();
        usuarioSesionDto.setRol("Admin");
        usuarioSesionDto.setUsername("admin@mail.com");

        // simular una sesion con usuario rol admin logueado
        when(sessionMock.getAttribute("usuarioLogueado")).thenReturn(usuarioSesionDto);

        // ******* EJECUCION
        ModelAndView modelAndView = controladorAdmin.mostrarDashboard(requestMock);
        String vistaObtenida = modelAndView.getViewName();

        // ******* VALIDACION
        assertThat(vistaObtenida, equalTo(vistaEsperada));
        assertThat(modelAndView.getModel().get("mailAdmin"), equalTo(usuarioSesionDto.getUsername()));

    }

    @Test
    public void queNoSePermitaElAccesoAlDashboardDelAdminSiNoHaySesionActiva() {
        // ******* PREPARACION
        String vistaEsperada = "redirect:/login";

        // Simular que el request tenga una sesion
        when(requestMock.getSession()).thenReturn(sessionMock);

        // simular una sesion sin usuario logueado
        when(sessionMock.getAttribute("usuarioLogueado")).thenReturn(null);

        // ******* EJECUCION
        ModelAndView modelAndView = controladorAdmin.mostrarDashboard(requestMock);
        String vistaObtenida = modelAndView.getViewName();

        // ******* VALIDACION
        assertThat(vistaObtenida, equalTo(vistaEsperada));

    }

    @Test
    public void queNoSePermitaElAccesoAlDashboardDelAdminSiElUsuarioNoEsAdmin() {
        // ******* PREPARACION
        String vistaEsperada = "redirect:/login";

        when(requestMock.getSession()).thenReturn(sessionMock);

        // Crear un usuario con rol PROVEEDOR
        UsuarioSesionDto proveedorSesionDto = new UsuarioSesionDto();
        proveedorSesionDto.setRol("PROVEEDOR");
        proveedorSesionDto.setUsername("prov@mail.com");
        proveedorSesionDto.setId(1L);

        // Simular una sesion con usuario rol PROVEEDOR logueado
        when(sessionMock.getAttribute("usuarioLogueado")).thenReturn(proveedorSesionDto);

        ModelAndView modelAndView = controladorAdmin.mostrarDashboard(requestMock);
        String vistaObtenida = modelAndView.getViewName();

        assertThat(vistaObtenida, equalTo(vistaEsperada));

    }

    @Test
    public void dadoUnAdminLogueadoQueSeMuestreLaCantidadTotalDeUsuarios(){
        // ******* PREPARACION
        String vistaEsperada = "dashboard-admin";

        // Simular que el request tenga una sesion
        when(requestMock.getSession()).thenReturn(sessionMock);

        // creo mi admin logueado
        UsuarioSesionDto adminSesionDto = new UsuarioSesionDto();
        adminSesionDto.setRol("Admin");
        adminSesionDto.setUsername("admin@email.com");
        adminSesionDto.setId(1L);

        // simular una sesion con usuario rol admin logueado
        when(sessionMock.getAttribute("usuarioLogueado")).thenReturn(adminSesionDto);

        //Valores esperados
        Integer esperadoTotalUsuarios = 50;
        Integer esperadoTotalProveedores = 15;
        Integer esperadoProveedoresPendientes = 3;
        Integer esperadoProveedoresRechazadas = 2;
        Integer esperadoTotalClientes = 35;

        // Lista simulada de usuarios (clientes y proveedores)
        List<Usuario> usuariosMock = Arrays.asList(
                new Cliente(),
                new Proveedor(),
                new Cliente()
        );

        // ******* EJECUCION
                // Simular respuestas de los servicios
        when(servicioUsuario.contarUsuarios()).thenReturn(esperadoTotalUsuarios);
        when(servicioProveedor.contarProveedores(null)).thenReturn(esperadoTotalProveedores);
        when(servicioProveedor.contarProveedores(EstadoUsuario.PENDIENTE)).thenReturn(esperadoProveedoresPendientes);
        when(servicioProveedor.contarProveedores(EstadoUsuario.RECHAZADO)).thenReturn(esperadoProveedoresRechazadas);
        when(servicioCliente.contarClientes()).thenReturn(esperadoTotalClientes);

        ModelAndView modelAndView = controladorAdmin.mostrarDashboard(requestMock);

        // ******* VALIDACION
        assertThat(modelAndView.getViewName(), equalTo(vistaEsperada));
        assertThat((Integer)modelAndView.getModel().get("totalUsuarios"), equalTo(esperadoTotalUsuarios));
        assertThat((Integer)modelAndView.getModel().get("totalProveedores"), equalTo(esperadoTotalProveedores));
        assertThat((Integer)modelAndView.getModel().get("proveedoresPendientes"), equalTo(esperadoProveedoresPendientes));
        assertThat((Integer)modelAndView.getModel().get("proveedoresRechazados"), equalTo(esperadoProveedoresRechazadas));
        assertThat((Integer)modelAndView.getModel().get("totalClientes"), equalTo(esperadoTotalClientes));
    }

}

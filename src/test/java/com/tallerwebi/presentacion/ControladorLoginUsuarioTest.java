package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

import com.tallerwebi.dominio.entidades.UsuarioAuth;
import com.tallerwebi.dominio.excepcion.UsuarioInexistenteException;
import com.tallerwebi.dominio.servicios.ServicioUsuarioAuthI;
import com.tallerwebi.presentacion.dto.UsuarioDto;

import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
/*
public class ControladorLoginUsuarioTest {

    private ControladorAuthLogin controladorLogin;
    private ServicioUsuarioAuthI servicioUsuarioI;
    private final String emailIngresado = "jesi@mail.com";
    private final String passwordIngresado = "Jesica12!";
    private HttpServletRequest requestMock;
    private HttpSession sessionMock;

    @BeforeEach
    public void init() {
        servicioUsuarioI = mock(ServicioUsuarioAuthI.class);
        controladorLogin = new ControladorAuthLogin(servicioUsuarioI);
        requestMock = mock(HttpServletRequest.class);
        sessionMock = mock(HttpSession.class);
    }

    @Test
    public void queSeMuestrePorPantallaElLoginParaQueSePuedaLoguearUsuario() {
        // preparacion
        String vistaEsperada = "login-usuario";

        // ejecucion
        ModelAndView modelAndView = controladorLogin.mostrarLogin();
        String vistaObtenida = modelAndView.getViewName();

        // validacion
        assertThat(vistaObtenida, equalTo(vistaEsperada));
        assertTrue(modelAndView.getModel().containsKey("usuarioDto"));
    }

    @Test
    public void dadoUnMailVacioQueElLoginFalle() {
        String vistaEsperada = "login-usuario";
        String msjErrorEsperado = "Por favor, ingresa el email.";
        UsuarioDto usuarioIngresado = new UsuarioDto("", passwordIngresado);

        ModelAndView vistaModelada = controladorLogin.procesarLogin(usuarioIngresado, requestMock);

        String vistaObtenida = vistaModelada.getViewName();
        String msjErrorObtenido = vistaModelada.getModel().get("error_email").toString();

        assertThat(vistaObtenida, equalTo(vistaEsperada));
        assertThat(msjErrorObtenido, equalTo(msjErrorEsperado));
    }

    @Test
    public void dadoUnMailConFormatoInvalidoQueElLoginFalle() {
        String vistaEsperada = "login-usuario";
        String msjErrorEsperado = "El formato del email es invalido";
        UsuarioDto usuarioIngresado = new UsuarioDto("jejej", passwordIngresado);

        ModelAndView vistaModelada = controladorLogin.procesarLogin(usuarioIngresado, requestMock);

        String vistaObtenida = vistaModelada.getViewName();
        String msjErroObtenido = vistaModelada.getModel().get("error_email").toString();

        assertThat(vistaObtenida, equalTo(vistaEsperada));
        assertThat(msjErroObtenido, equalTo(msjErrorEsperado));
    }

    @Test
    public void dadoUnaContraseniaVaciaQueElLoginFalle() {

        String vistaEsperada = "login-usuario";
        String msjErrorEsperado = "Por favor, ingresa la contraseña.";
        UsuarioDto usuarioIngresado = new UsuarioDto("jesi@gmail.com", "");

        ModelAndView vistaModelada = controladorLogin.procesarLogin(usuarioIngresado, requestMock);

        String vistaObtenida = vistaModelada.getViewName();
        String msjErrorObtenido = vistaModelada.getModel().get("error_password").toString();

        assertThat(vistaObtenida, equalTo(vistaEsperada));
        assertThat(msjErrorObtenido, equalTo(msjErrorEsperado));

    }

    // simular acciones de servicios
    @Test
    public void dadoUnProveedorConCredencialesValidasDebeDirigirseADashboardProveedor() throws UsuarioInexistenteException{
        UsuarioAuth usuarioEncontradoMock = mock(UsuarioAuth.class);
        UsuarioDto usuarioDto = new UsuarioDto(emailIngresado, passwordIngresado);

        when(usuarioEncontradoMock.getRol()).thenReturn("Proveedor");

        when(requestMock.getSession()).thenReturn(sessionMock);

        when(servicioUsuarioI.autenticar(emailIngresado, passwordIngresado)).thenReturn(usuarioEncontradoMock);

        ModelAndView modelAndView = controladorLogin.procesarLogin(usuarioDto, requestMock);

        assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/proveedor/dashboard-proveedor"));
        
    }

    @Test
    public void dadoUnClienteConCredencialesValidasDebeDirigirseADashboardCliente() throws UsuarioInexistenteException {
        UsuarioAuth usuarioEncontradoMock = mock(UsuarioAuth.class);
        UsuarioDto usuarioDto = new UsuarioDto(emailIngresado, passwordIngresado);

        when(usuarioEncontradoMock.getRol()).thenReturn("Cliente");

        when(requestMock.getSession()).thenReturn(sessionMock);

        when(servicioUsuarioI.autenticar(emailIngresado, passwordIngresado)).thenReturn(usuarioEncontradoMock);

        ModelAndView modelAndView = controladorLogin.procesarLogin(usuarioDto, requestMock);

        assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/cliente/dashboard"));

    }

    @Test
    public void dadoUnMailInexisteCuandoElUsuarioSeLogueeEntoncesElLoginFalle() throws UsuarioInexistenteException{
        
        doThrow(UsuarioInexistenteException.class).when(servicioUsuarioI).autenticar("belen@gmail.com", "hola");
    }

    @Test
    public void queSePuedaCerrarSesionYRedirigirAlLogin(){

        //preparacion
        String redireccionEsperada = "redirect:/login-user";
        when(requestMock.getSession()).thenReturn(sessionMock); //simular sesion activa

        //ejecucion
        String vistaObtenida = controladorLogin.logOut(requestMock);

        //validacion
        verify(sessionMock, times(1)).invalidate(); //Verificamos que se invoco 1 vez el metodo invalidate para matar la sesion
        assertThat(vistaObtenida, equalTo(redireccionEsperada));
        
    }


}
*/
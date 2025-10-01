package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.excepcion.ContraseniaInvalida;
import com.tallerwebi.dominio.excepcion.EmailInvalido;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import com.tallerwebi.dominio.servicios.ServicioLogin;
import com.tallerwebi.presentacion.dto.DatosLogin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ControladorAutenticacionTest {

	private ControladorAutenticacion controladorAutenticacion;
	private Usuario usuarioMock;
	private DatosLogin datosLoginMock;
	private HttpServletRequest requestMock;
	private HttpSession sessionMock;
	private ServicioLogin servicioLoginMock;

	@BeforeEach
    public void init() {
        datosLoginMock = new DatosLogin("dami@unlam.com", "123");
        usuarioMock = mock(Usuario.class);
        when(usuarioMock.getEmail()).thenReturn("dami@unlam.com");
        when(usuarioMock.getPassword()).thenReturn("123");
        requestMock = mock(HttpServletRequest.class);
        sessionMock = mock(HttpSession.class);
        servicioLoginMock = mock(ServicioLogin.class);
        controladorAutenticacion = new ControladorAutenticacion(servicioLoginMock);
 
        when(requestMock.getParameter("confirmarPassword")).thenReturn("123");
    }
	//## Test para ir a LOGIN

	@Test
	public void loginConUsuarioYPasswordInorrectosDeberiaLlevarALoginNuevamente() {
		// preparacion
		when(servicioLoginMock.consultarUsuario(anyString(), anyString())).thenReturn(null);

		// ejecucion
		ModelAndView modelAndView = controladorAutenticacion.validarLogin(datosLoginMock, requestMock);

		// validacion
		assertThat(modelAndView.getViewName(), equalToIgnoringCase("login"));
		assertThat(modelAndView.getModel().get("error").toString(), equalToIgnoringCase("Usuario o clave incorrecta"));
		verify(sessionMock, times(0)).setAttribute("ROL", "ADMIN");
	}

	@Test
	public void loginConUsuarioYPasswordCorrectosDeberiaLLevarAHome() {
		// preparacion
		Usuario usuarioEncontradoMock = mock(Usuario.class);
		when(usuarioEncontradoMock.getRol()).thenReturn("ADMIN");

		when(requestMock.getSession()).thenReturn(sessionMock);
		when(servicioLoginMock.consultarUsuario(anyString(), anyString())).thenReturn(usuarioEncontradoMock);

		// ejecucion
		ModelAndView modelAndView = controladorAutenticacion.validarLogin(datosLoginMock, requestMock);

		// validacion
		assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/admin/dashboard-admin"));
		verify(sessionMock, times(1)).setAttribute("ROL", usuarioEncontradoMock.getRol());
	}

	// ## Test para REGISTRARME COMO CLIENTE
	@Test
	public void registrameSiUsuarioNoExisteDeberiaCrearUsuarioYVolverAlLogin()
			throws UsuarioExistente, ContraseniaInvalida, EmailInvalido {

		// ejecucion
		ModelAndView modelAndView = controladorAutenticacion.registrarme(usuarioMock, requestMock);

		// validacion
		assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/login"));
		verify(servicioLoginMock, times(1)).registrar(usuarioMock);
	}

	@Test
	public void registrarmeSiUsuarioExisteDeberiaVolverAFormularioYMostrarError()
			throws UsuarioExistente, ContraseniaInvalida, EmailInvalido {
		// preparacion
		doThrow(UsuarioExistente.class).when(servicioLoginMock).registrar(usuarioMock);

		// ejecucion
		ModelAndView modelAndView = controladorAutenticacion.registrarme(usuarioMock, requestMock);

		// validacion
		assertThat(modelAndView.getViewName(), equalToIgnoringCase("nuevo-usuario"));
		assertThat(modelAndView.getModel().get("error").toString(), equalToIgnoringCase("El usuario ya existe"));
	}

	@Test
	public void errorEnRegistrarmeDeberiaVolverAFormularioYMostrarError()
			throws UsuarioExistente, ContraseniaInvalida, EmailInvalido {
		// preparacion
		doThrow(RuntimeException.class).when(servicioLoginMock).registrar(usuarioMock);

		// ejecucion
		ModelAndView modelAndView = controladorAutenticacion.registrarme(usuarioMock, requestMock);

		// validacion
		assertThat(modelAndView.getViewName(), equalToIgnoringCase("nuevo-usuario"));
		assertThat(modelAndView.getModel().get("error").toString(),
				equalToIgnoringCase("Error al registrar el nuevo usuario"));
	}

	// ## Test para REGISTRARME COMO PROVEEDOR
	@Test
	public void dadoQueUnUsuarioQuieraRegistrarseComoProveedorQueSeMuestreElFormularioDeProveedor(){
		//Preoparacion
		String vistaEsperada = "nuevo-proveedor";
		//Ejecucion
		ModelAndView modelAndView = controladorAutenticacion.irRegistroProveedor();
		String vistaObtenida = modelAndView.getViewName();
		
		//Validacion
		assertThat(vistaObtenida, equalToIgnoringCase(vistaEsperada));
		assertTrue(modelAndView.getModel().containsKey("usuarioProveedorDto"));

	}

}

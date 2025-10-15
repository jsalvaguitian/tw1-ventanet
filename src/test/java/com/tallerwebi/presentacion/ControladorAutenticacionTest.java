package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.entidades.Cliente;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.excepcion.ContraseniaInvalida;
import com.tallerwebi.dominio.excepcion.EmailInvalido;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import com.tallerwebi.dominio.excepcion.UsuarioInexistenteException;
import com.tallerwebi.dominio.servicios.ServicioUsuario;
import com.tallerwebi.presentacion.dto.DatosLogin;
import com.tallerwebi.presentacion.dto.UsuarioSesionDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ControladorAutenticacionTest {

	private ControladorAutenticacion controladorLogin;
	private Cliente usuarioMock;
	private UsuarioSesionDto usuarioSesionMock;
	private DatosLogin datosLoginMock;
	private HttpServletRequest requestMock;
	private HttpSession sessionMock;
	private ServicioUsuario servicioUsuarioMock;

	@BeforeEach
	public void init() {
		datosLoginMock = new DatosLogin("dami@unlam.com", "123");
		usuarioMock = mock(Cliente.class);
		usuarioSesionMock = mock(UsuarioSesionDto.class);
		when(usuarioMock.getEmail()).thenReturn("dami@unlam.com");
		when(usuarioMock.getPassword()).thenReturn("123");
		requestMock = mock(HttpServletRequest.class);
		sessionMock = mock(HttpSession.class);
		servicioUsuarioMock = mock(ServicioUsuario.class);
		controladorLogin = new ControladorAutenticacion(servicioUsuarioMock);

		when(requestMock.getParameter("confirmarPassword")).thenReturn("123");
	}

	@Test
	public void loginConUsuarioYPasswordInorrectosDeberiaLlevarALoginNuevamente() throws UsuarioInexistenteException {
		// preparacion
		when(servicioUsuarioMock.consultarUsuario(anyString(), anyString()))
				.thenThrow(new UsuarioInexistenteException());

		// Ejecución
		ModelAndView modelAndView = controladorLogin.validarLogin(datosLoginMock, requestMock);

		// Validación
		assertThat(modelAndView.getViewName(), equalToIgnoringCase("login"));
		assertThat(modelAndView.getModel().get("error").toString(),
				equalToIgnoringCase("Usuario o clave incorrecta"));
	}

	@Test
	public void loginConUsuarioYPasswordCorrectosDeberiaLLevarAHome() throws UsuarioInexistenteException {
		// preparacion
		Usuario usuarioEncontradoMock = mock(Usuario.class);
		when(usuarioEncontradoMock.getRol()).thenReturn("ADMIN");

		when(requestMock.getSession()).thenReturn(sessionMock);

		when(servicioUsuarioMock.consultarUsuario(anyString(), anyString())).thenReturn(usuarioEncontradoMock);

		// ejecucion
		ModelAndView modelAndView = controladorLogin.validarLogin(datosLoginMock, requestMock);

		// validacion
		assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/admin/dashboard-admin"));
		verify(sessionMock, times(1)).setAttribute(eq("usuarioLogueado"), any(UsuarioSesionDto.class));
	}

	@Test
	public void registrameSiUsuarioNoExisteDeberiaCrearUsuarioYVolverAlLogin()
			throws UsuarioExistente, ContraseniaInvalida, EmailInvalido {

		// ejecucion
		ModelAndView modelAndView = controladorLogin.registrarme(usuarioMock, requestMock);

		// validacion
		assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/login"));
		verify(servicioUsuarioMock, times(1)).registrar(any(Usuario.class));
	}

	@Test
	public void registrarmeSiUsuarioExisteDeberiaVolverAFormularioYMostrarError()
			throws UsuarioExistente, ContraseniaInvalida, EmailInvalido {
		// preparacion
		doThrow(UsuarioExistente.class).when(servicioUsuarioMock).registrar(any(Cliente.class));

		// ejecucion
		ModelAndView modelAndView = controladorLogin.registrarme(usuarioMock, requestMock);

		// validacion
		assertThat(modelAndView.getViewName(), equalToIgnoringCase("nuevo-usuario"));
		assertThat(modelAndView.getModel().get("error").toString(), equalToIgnoringCase("El usuario ya existe"));
	}

	@Test
	public void errorEnRegistrarmeDeberiaVolverAFormularioYMostrarError()
			throws UsuarioExistente, ContraseniaInvalida, EmailInvalido {
		// preparacion
		doThrow(RuntimeException.class).when(servicioUsuarioMock).registrar(any(Cliente.class));

		// ejecucion
		ModelAndView modelAndView = controladorLogin.registrarme(usuarioMock, requestMock);

		// validacion
		assertThat(modelAndView.getViewName(), equalToIgnoringCase("nuevo-usuario"));
		assertThat(modelAndView.getModel().get("error").toString(),
				equalToIgnoringCase("Error al registrar el nuevo usuario"));
	}

}

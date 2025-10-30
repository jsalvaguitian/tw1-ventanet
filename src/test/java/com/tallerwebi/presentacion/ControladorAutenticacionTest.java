package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.entidades.Cliente;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.excepcion.ContraseniaInvalida;
import com.tallerwebi.dominio.excepcion.CuentaNoActivaException;
import com.tallerwebi.dominio.excepcion.CuentaPendienteException;
import com.tallerwebi.dominio.excepcion.CuentaRechazadaException;
import com.tallerwebi.dominio.excepcion.EmailInvalido;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import com.tallerwebi.dominio.excepcion.UsuarioInexistenteException;
import com.tallerwebi.dominio.servicios.ServicioUsuario;
import com.tallerwebi.presentacion.dto.DatosLogin;
import com.tallerwebi.presentacion.dto.UsuarioSesionDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
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
	private RedirectAttributes redirectAttributesMock;

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
		redirectAttributesMock = mock(RedirectAttributes.class);

		when(requestMock.getParameter("confirmarPassword")).thenReturn("123");
	}

	@Test
	public void loginConUsuarioYPasswordInorrectosDeberiaLlevarALoginNuevamente() throws UsuarioInexistenteException, CuentaNoActivaException, CuentaPendienteException, CuentaRechazadaException {
		// preparacion
		when(servicioUsuarioMock.iniciarSesion(anyString(), anyString()))
				.thenThrow(new UsuarioInexistenteException());

		// Ejecución
		ModelAndView modelAndView = controladorLogin.validarLogin(datosLoginMock, requestMock);

		// Validación
		assertThat(modelAndView.getViewName(), equalToIgnoringCase("login"));
		assertThat(modelAndView.getModel().get("error").toString(),
				equalToIgnoringCase("Usuario o clave incorrecta"));
	}

	@Test
	public void loginAdminConUsuarioYPasswordCorrectosDeberiaLLevarAlDashboard() throws UsuarioInexistenteException, CuentaNoActivaException, CuentaPendienteException, CuentaRechazadaException {
		// preparacion
		Usuario usuarioEncontradoMock = mock(Usuario.class);
		when(usuarioEncontradoMock.getRol()).thenReturn("ADMIN");

		when(requestMock.getSession()).thenReturn(sessionMock);

		when(servicioUsuarioMock.iniciarSesion(anyString(), anyString())).thenReturn(usuarioEncontradoMock);

		// ejecucion
		ModelAndView modelAndView = controladorLogin.validarLogin(datosLoginMock, requestMock);

		// validacion
		assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/admin/dashboard-admin"));
		verify(sessionMock, times(1)).setAttribute(eq("usuarioLogueado"), any(UsuarioSesionDto.class));
	}

	@Test
	public void queSePuedaMostrarElFormularioDeLogin(){
		//preparacion
		String vistaEsperada = "login";
		//ejecucion
		ModelAndView modelAndView = controladorLogin.irALogin();

		//validacion
		assertThat(modelAndView.getViewName(), equalToIgnoringCase(vistaEsperada));
		assertThat(modelAndView.getModel().get("datosLogin"), instanceOf(DatosLogin.class));
	}

	@Test
	public void dadoUnUsuarioProveedorConCredencialesValidasDeberiaRedirigirAlDashboardProveedor() throws UsuarioInexistenteException, CuentaNoActivaException, CuentaPendienteException, CuentaRechazadaException {
		// preparacion
		Usuario usuarioEncontradoMock = mock(Usuario.class);
		when(usuarioEncontradoMock.getRol()).thenReturn("PROVEEDOR");

		when(requestMock.getSession()).thenReturn(sessionMock);

		when(servicioUsuarioMock.iniciarSesion(anyString(), anyString())).thenReturn(usuarioEncontradoMock);

		// ejecucion
		ModelAndView modelAndView = controladorLogin.validarLogin(datosLoginMock, requestMock);

		// validacion
		assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/proveedor/dashboard-proveedor"));
		verify(sessionMock, times(1)).setAttribute(eq("usuarioLogueado"), any(UsuarioSesionDto.class));
	}

	@Test
	public void dadoUnUsuarioClienteConCredencialesValidasDeberiaRedirigirAlDashboard() throws UsuarioInexistenteException, CuentaNoActivaException, CuentaPendienteException, CuentaRechazadaException {
		// preparacion
		Usuario usuarioEncontradoMock = mock(Usuario.class);
		when(usuarioEncontradoMock.getRol()).thenReturn("CLIENTE");

		when(requestMock.getSession()).thenReturn(sessionMock);

		when(servicioUsuarioMock.iniciarSesion(anyString(), anyString())).thenReturn(usuarioEncontradoMock);

		// ejecucion
		ModelAndView modelAndView = controladorLogin.validarLogin(datosLoginMock, requestMock);

		// validacion
		assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/cliente/dashboard"));
		verify(sessionMock, times(1)).setAttribute(eq("usuarioLogueado"), any(UsuarioSesionDto.class));
	}

	@Test
	public void dadoUnClienteConCuentaNoActivaDeberiaMostrarErrorEnLogin() throws UsuarioInexistenteException, CuentaNoActivaException, CuentaPendienteException, CuentaRechazadaException {
		// preparacion
		when(servicioUsuarioMock.iniciarSesion(anyString(), anyString()))
				.thenThrow(new CuentaNoActivaException());

		// Ejecución
		ModelAndView modelAndView = controladorLogin.validarLogin(datosLoginMock, requestMock);

		// Validación
		assertThat(modelAndView.getViewName(), equalToIgnoringCase("login"));
		assertThat(modelAndView.getModel().get("error").toString(),
				equalToIgnoringCase("Debes verificar tu correo electrónico para activar tu cuenta."));
	}

	@Test
	public void dadoUnProveedorConCuentaPendienteDeberiaMostrarErrorEnLogin() throws UsuarioInexistenteException, CuentaNoActivaException, CuentaPendienteException, CuentaRechazadaException {
		// preparacion
		when(servicioUsuarioMock.iniciarSesion(anyString(), anyString()))
				.thenThrow(new CuentaPendienteException());

		// Ejecución
		ModelAndView modelAndView = controladorLogin.validarLogin(datosLoginMock, requestMock);

		// Validación
		assertThat(modelAndView.getViewName(), equalToIgnoringCase("login"));
		assertThat(modelAndView.getModel().get("error").toString(),
				equalToIgnoringCase("Tu cuenta está pendiente de aprobación por el administrador."));
	}

	@Test
	public void dadoUnProveedorConCuentaRechazadaDeberiaMostrarUnErrorEnLogin() throws UsuarioInexistenteException, CuentaNoActivaException, CuentaPendienteException, CuentaRechazadaException{
		//preparacion
		when(servicioUsuarioMock.iniciarSesion(anyString(), anyString())).thenThrow(new CuentaRechazadaException());

		//ejecucion
		ModelAndView modelAndView = controladorLogin.validarLogin(datosLoginMock, requestMock);

		//validacion
		assertThat(modelAndView.getViewName(),equalToIgnoringCase("login"));
		assertThat(modelAndView.getModel().get("error").toString(), equalToIgnoringCase("Tu solicitud fue rechazada. Contacta al soporte."));
	}

	//********************* REGISTRO ************************* */
	@Test
	public void registrameSiUsuarioNoExisteDeberiaCrearUsuarioYVolverAlLogin()
			throws UsuarioExistente, ContraseniaInvalida, EmailInvalido {

		// ejecucion
		ModelAndView modelAndView = controladorLogin.registrarme(usuarioMock, requestMock,redirectAttributesMock);

		// validacion
		assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/info-registro-resultado"));
		verify(servicioUsuarioMock, times(1)).registrar(any(Cliente.class));
		verify(redirectAttributesMock, times(1)).addFlashAttribute("tipoUsuario", "cliente");
	}

	@Test
	public void registrarmeSiUsuarioExisteDeberiaVolverAFormularioYMostrarError()
			throws UsuarioExistente, ContraseniaInvalida, EmailInvalido {
		// preparacion
		doThrow(UsuarioExistente.class).when(servicioUsuarioMock).registrar(any(Cliente.class));

		// ejecucion
		ModelAndView modelAndView = controladorLogin.registrarme(usuarioMock, requestMock, null);

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
		ModelAndView modelAndView = controladorLogin.registrarme(usuarioMock, requestMock, null);

		// validacion
		assertThat(modelAndView.getViewName(), equalToIgnoringCase("nuevo-usuario"));
		assertThat(modelAndView.getModel().get("error").toString(),
				equalToIgnoringCase("Error al registrar el nuevo usuario"));
	}

}

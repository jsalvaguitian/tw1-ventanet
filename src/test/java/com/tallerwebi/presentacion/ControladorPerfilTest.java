package com.tallerwebi.presentacion;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.tallerwebi.dominio.entidades.Cliente;
import com.tallerwebi.dominio.entidades.Proveedor;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.excepcion.UsuarioInexistenteException;
import com.tallerwebi.dominio.excepcion.ValorInvalido;
import com.tallerwebi.dominio.servicios.ServicioCotizacion;
import com.tallerwebi.dominio.servicios.ServicioPerfil;
import com.tallerwebi.dominio.servicios.ServicioUsuario;
import com.tallerwebi.presentacion.dto.UsuarioSesionDto;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.hamcrest.Matchers.*;

public class ControladorPerfilTest {
    private ControladorPerfil controlador;
    private ServicioUsuario servicioUsuario;
    private ServicioPerfil servicioPerfil;
    private ServicioCotizacion servicioCotizacion;
    private MockMultipartFile mockFile;

    @BeforeEach
    public void init() {
        servicioPerfil = mock(ServicioPerfil.class);
        servicioUsuario = mock(ServicioUsuario.class);
        servicioCotizacion = mock(ServicioCotizacion.class);
        controlador = new ControladorPerfil(servicioUsuario, servicioPerfil, servicioCotizacion);
        mockFile = new MockMultipartFile(
                "foto",
                "imagen.jpg",
                "image/jpeg",
                "imagen".getBytes());

    }

    @Test
    public void CuandoElUsuarioEsClienteMostrarPerfilParaClientes() throws UsuarioInexistenteException {
        UsuarioSesionDto usuarioSesion = new UsuarioSesionDto();
        usuarioSesion.setId(1L);

        Usuario usuario = new Cliente();
        usuario.setId(1L);
        usuario.setNombre("Juan");
        usuario.setApellido("Pérez");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession().setAttribute("usuarioLogueado", usuarioSesion);

        when(servicioUsuario.buscarPorId(1L)).thenReturn(usuario);

        ModelAndView modelAndView = controlador.mostrarPerfil(request);
        verify(servicioUsuario, times(1)).buscarPorId(1L);
        assertThat(modelAndView.getViewName(), equalToIgnoringCase("perfil-usuario"));
        assertTrue(modelAndView.getModel().containsKey("usuario"));
    }

    @Test
    public void CuandoElUsuarioEsProveedorMostrarPerfilParaProveedores() throws UsuarioInexistenteException {
        UsuarioSesionDto usuarioSesion = new UsuarioSesionDto();
        usuarioSesion.setId(1L);

        Usuario usuario = new Proveedor();
        usuario.setId(1L);
        usuario.setNombre("Juan");
        usuario.setApellido("Pérez");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession().setAttribute("usuarioLogueado", usuarioSesion);

        when(servicioUsuario.buscarPorId(1L)).thenReturn(usuario);

        ModelAndView modelAndView = controlador.mostrarPerfil(request);

        verify(servicioUsuario, times(1)).buscarPorId(1L);
        assertThat(modelAndView.getViewName(), equalToIgnoringCase("perfil-proveedor"));
        assertTrue(modelAndView.getModel().containsKey("usuario"));
    }

    @Test
    public void CuandoElUsuarioNoEsteLogueadoDeberiaRederigirAlLogin() throws UsuarioInexistenteException {
        UsuarioSesionDto usuarioSesion = new UsuarioSesionDto();
        usuarioSesion = null;

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession().setAttribute("usuarioLogueado", usuarioSesion);

        ModelAndView modelAndView = controlador.mostrarPerfil(request);

        verify(servicioUsuario, times(0)).buscarPorId(anyLong());
        assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/login"));
    }

    @Test
    public void SiElUsuarioNoTieneFotoDePerfilPonerUnaImagenDefault() throws UsuarioInexistenteException {
        UsuarioSesionDto usuarioSesion = new UsuarioSesionDto();
        usuarioSesion.setId(1L);

        Usuario usuario = new Cliente();
        usuario.setId(1L);
        usuario.setFotoPerfil(null);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession().setAttribute("usuarioLogueado", usuarioSesion);

        when(servicioUsuario.buscarPorId(1L)).thenReturn(usuario);

        ModelAndView modelAndView = controlador.mostrarPerfil(request);

        assertTrue(modelAndView.getModel().containsKey("fotoPerfil"));
        String fotoPerfil = (String) modelAndView.getModel().get("fotoPerfil");
        assertEquals("/img/default-profile.png", fotoPerfil);
    }

    @Test
    public void AlCambiarFotoDePerfilDebeRedirigirAlPerfilUsuario() throws UsuarioInexistenteException, IOException {
        UsuarioSesionDto usuarioSesion = new UsuarioSesionDto();
        usuarioSesion.setId(1L);

        Usuario usuario = new Cliente();
        usuario.setId(1L);
        usuario.setFotoPerfil(null);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession().setAttribute("usuarioLogueado", usuarioSesion);

        when(servicioUsuario.buscarPorId(1L)).thenReturn(usuario);
        when(servicioPerfil.cambiarFotoPerfil(mockFile, usuario)).thenReturn(true);

        ModelAndView modelAndView = controlador.cambiarFoto(request, mockFile);

        verify(servicioUsuario, times(1)).buscarPorId(anyLong());
        verify(servicioPerfil, times(1)).cambiarFotoPerfil(mockFile, usuario);
        assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/usuarios/perfil-usuario"));
    }

    @Test
    public void SiElUsuarioNoEstaLogueadoAlEditarPerfilLoRedirigeAlLogin() throws UsuarioInexistenteException {
        UsuarioSesionDto usuarioSesion = new UsuarioSesionDto();
        usuarioSesion = null;

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession().setAttribute("usuarioLogueado", usuarioSesion);

        ModelAndView modelAndView = controlador.editarPerfil(request);

        verify(servicioUsuario, times(0)).buscarPorId(anyLong());
        assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/login"));
    }

    @Test
    public void SiElUsuarioEstaLogueadoAlEditarPerfilMuestreElFormulario() throws UsuarioInexistenteException {
        UsuarioSesionDto usuarioSesion = new UsuarioSesionDto();
        usuarioSesion.setId(1L);

        Usuario usuario = new Cliente();
        usuario.setId(1L);
        usuario.setNombre("Juan");
        usuario.setApellido("Pérez");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession().setAttribute("usuarioLogueado", usuarioSesion);

        when(servicioUsuario.buscarPorId(1L)).thenReturn(usuario);

        ModelAndView modelAndView = controlador.editarPerfil(request);

        assertThat(modelAndView.getViewName(), equalToIgnoringCase("editar-perfil"));
        assertTrue(modelAndView.getModel().containsKey("usuario"));
    }

    @Test
    public void SiElUsuarioNoEstaLogueadoAlActualizarPerfilLoRedirigeAlLogin()
            throws UsuarioInexistenteException, ValorInvalido {
        UsuarioSesionDto usuarioSesion = new UsuarioSesionDto();
        usuarioSesion = null;

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession().setAttribute("usuarioLogueado", usuarioSesion);

        ModelAndView modelAndView = controlador.actualizarPerfil("Jhon", "Cena", "Jhon cena", "123", "Calle 123",
                null, null, null, request);

        verify(servicioUsuario, times(0)).buscarPorId(anyLong());
        verifyNoInteractions(servicioPerfil);
        assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/login"));
    }

    @Test
    public void SiElUsuarioEstaLogueadoAlActualizarPerfilLoLlevaAlPerfil()
            throws UsuarioInexistenteException, ValorInvalido {
        UsuarioSesionDto usuarioSesion = new UsuarioSesionDto();
        usuarioSesion.setId(1L);

        Usuario usuario = new Cliente();
        usuario.setId(1L);
        usuario.setNombre("Juan");
        usuario.setApellido("Pérez");
        usuario.setRol("CLIENTE");
        usuario.setDireccion(null);
        usuario.setUsername("Natan");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession().setAttribute("usuarioLogueado", usuarioSesion);

        when(servicioUsuario.buscarPorId(1L)).thenReturn(usuario);

        ModelAndView modelAndView = controlador.actualizarPerfil("Jhon", "Cena", "Jhon cena", "123", "Calle 123",
                null, null, null, request);

        verify(servicioPerfil, times(1)).actualizarPerfil("Jhon", "Cena", "Jhon cena", "Calle 123", "123",
                usuario);
        verify(servicioUsuario, times(1)).buscarPorId(1L);
        // assertThat(request.getSession().getAttribute("usuarioLogueado"),
        // equalTo(usuario));
        assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/usuarios/perfil-usuario"));
    }

    @Test
    public void QueSeElimineUnUsuario() throws UsuarioInexistenteException {
        UsuarioSesionDto usuarioSesion = new UsuarioSesionDto();
        usuarioSesion.setId(1L);

        Usuario usuario = new Cliente();
        usuario.setId(1L);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession().setAttribute("usuarioLogueado", usuarioSesion);

        when(servicioUsuario.buscarPorId(1L)).thenReturn(usuario);

        ModelAndView modelAndView = controlador.eliminarPerfil(request);

        verify(servicioUsuario, times(1)).buscarPorId(1L);
        verify(servicioUsuario, times(1)).eliminarUsuario(usuario);
        assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/login"));
    }

    @Test
    public void SiElUsuarioEstaLogueadoYLaImagenNoEsValidaRetornarError()
            throws UsuarioInexistenteException, IOException {
        UsuarioSesionDto usuarioSesion = new UsuarioSesionDto();
        usuarioSesion.setId(1L);

        Usuario usuario = new Cliente();
        usuario.setId(1L);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession().setAttribute("usuarioLogueado", usuarioSesion);

        when(servicioUsuario.buscarPorId(1L)).thenReturn(usuario);
        when(servicioPerfil.cambiarFotoPerfil(null, usuario)).thenReturn(false);

        ModelAndView modelAndView = controlador.cambiarFoto(request, null);

        verify(servicioUsuario, times(1)).buscarPorId(1L);
        verify(servicioPerfil, times(1)).cambiarFotoPerfil(null, usuario);
        assertThat(modelAndView.getModel().get("error"), equalTo("A ocurrido un error"));
        assertThat(modelAndView.getViewName(), equalToIgnoringCase("perfil-usuario"));
    }

    @Test
    public void SiElUsuarioNoEstaLogueadoAlCambiarFotoRedirigirAlLogin()
            throws UsuarioInexistenteException, IOException {
        UsuarioSesionDto usuarioSesion = new UsuarioSesionDto();
        usuarioSesion = null;

        Usuario usuario = new Cliente();
        usuario.setId(1L);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession().setAttribute("usuarioLogueado", usuarioSesion);

        ModelAndView modelAndView = controlador.cambiarFoto(request, null);

        verify(servicioUsuario, times(0)).buscarPorId(1L);
        verify(servicioPerfil, times(0)).cambiarFotoPerfil(null, usuario);
        assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/login"));
    }

    @Test
    public void SiCambiarFotoLanzaIOExceptionDebeMostrarMensajeDeError()
            throws UsuarioInexistenteException, IOException {
        UsuarioSesionDto usuarioSesion = new UsuarioSesionDto();
        usuarioSesion.setId(1L);

        Usuario usuario = new Cliente();
        usuario.setId(1L);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession().setAttribute("usuarioLogueado", usuarioSesion);

        MultipartFile mockFile = mock(MultipartFile.class);

        when(servicioUsuario.buscarPorId(1L)).thenReturn(usuario);
        when(servicioPerfil.cambiarFotoPerfil(mockFile, usuario)).thenThrow(new IOException("Error al leer archivo"));

        ModelAndView mav = controlador.cambiarFoto(request, mockFile);

        assertThat(mav.getViewName(), equalToIgnoringCase("perfil-usuario"));
        assertThat(mav.getModel().get("error"), equalTo("Error al subir la imagen"));
    }

    // perfil proveedor
    @Test
    public void SiElUsuarioEsProveedorYNoTieneCotizacionesMostrarMensaje() throws UsuarioInexistenteException {
        UsuarioSesionDto usuarioSesion = new UsuarioSesionDto();
        usuarioSesion.setId(1L);

        Usuario usuario = new Proveedor();
        usuario.setId(1L);
        usuario.setNombre("Juan");
        usuario.setApellido("Pérez");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession().setAttribute("usuarioLogueado", usuarioSesion);

        when(servicioUsuario.buscarPorId(1L)).thenReturn(usuario);
        when(servicioCotizacion.obtenerEstadisticasCotizacionesDelProveedor(1L)).thenReturn(null);

        ModelAndView modelAndView = controlador.mostrarPerfil(request);

        verify(servicioUsuario, times(1)).buscarPorId(1L);
        assertThat(modelAndView.getModel().get("graficoVacio"),
                equalTo("No tienes cotizaciones para mostrar estadísticas aún."));
        assertThat(modelAndView.getViewName(), equalToIgnoringCase("perfil-proveedor"));
        assertTrue(modelAndView.getModel().containsKey("usuario"));
    }
}

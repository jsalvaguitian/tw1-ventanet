package com.tallerwebi.presentacion;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.excepcion.UsuarioInexistenteException;
import com.tallerwebi.dominio.servicios.ServicioNotificacion;
import com.tallerwebi.dominio.servicios.ServicioUsuario;
import com.tallerwebi.presentacion.dto.NotificacionDTO;
import com.tallerwebi.presentacion.dto.UsuarioSesionDto;

@RestController
@RequestMapping("/api/notificaciones")
public class ControladorNotificacion {

    private final ServicioNotificacion servicioNotificacion;
    private final ServicioUsuario servicioUsuario;

    @Autowired
    public ControladorNotificacion(ServicioNotificacion servicioNotificacion, ServicioUsuario servicioUsuario) {
        this.servicioNotificacion = servicioNotificacion;
        this.servicioUsuario = servicioUsuario;
    }

    @GetMapping("/no-leidas")
    public List<NotificacionDTO> traerNoLeidas(HttpServletRequest httpServletRequest)
            throws UsuarioInexistenteException {

        UsuarioSesionDto usuarioSesion = (UsuarioSesionDto) httpServletRequest.getSession()
                .getAttribute("usuarioLogueado");

        Usuario usuarioCompleto = servicioUsuario.buscarPorId(usuarioSesion.getId());

        return servicioNotificacion.obtenerNoLeidas(usuarioCompleto.getId(), 10)
                .stream()
                .map(NotificacionDTO::new)
                .collect(Collectors.toList());
    }

    @PostMapping("/{id}/marcar-leida")
    public void marcarLeida(@PathVariable Long id, HttpServletRequest httpServletRequest) {
        servicioNotificacion.marcarComoLeida(id);
    }

    @GetMapping("/contar")
    public Long contarNoLeidas(HttpServletRequest httpServletRequest) throws UsuarioInexistenteException {
        UsuarioSesionDto usuarioSesion = (UsuarioSesionDto) httpServletRequest.getSession()
                .getAttribute("usuarioLogueado");

        if (usuarioSesion == null) {
            return 0L;
        }

        Usuario usuarioCompleto = servicioUsuario.buscarPorId(usuarioSesion.getId());
        return servicioNotificacion.contarNoLeidas(usuarioCompleto.getId());
    }

    @PostMapping("/marcar-todas")
    public void marcarTodas(HttpServletRequest httpServletRequest) throws UsuarioInexistenteException {
        UsuarioSesionDto usuarioSesion = (UsuarioSesionDto) httpServletRequest.getSession()
                .getAttribute("usuarioLogueado");
        Usuario usuarioCompleto = servicioUsuario.buscarPorId(usuarioSesion.getId());
        servicioNotificacion.marcarTodasComoLeidas(usuarioCompleto.getId());
    }

    @GetMapping("/todas")
    public ModelAndView mostrarTodasLasNotificaciones(HttpServletRequest httpServletRequest)
            throws UsuarioInexistenteException {
        UsuarioSesionDto usuarioSesion = (UsuarioSesionDto) httpServletRequest.getSession()
                .getAttribute("usuarioLogueado");

        ModelMap modelMap = new ModelMap();
        if (usuarioSesion == null) {
            return new ModelAndView("redirect:/login", modelMap);
        }

        return new ModelAndView("ListaNotificaciones");
    }

    @GetMapping("/todas-las-notificaciones")
    public List<NotificacionDTO> traerTodasLasNotificaciones(HttpServletRequest httpServletRequest)
            throws UsuarioInexistenteException {

        UsuarioSesionDto usuarioSesion = (UsuarioSesionDto) httpServletRequest.getSession()
                .getAttribute("usuarioLogueado");

        Usuario usuarioCompleto = servicioUsuario.buscarPorId(usuarioSesion.getId());

        return servicioNotificacion.obtenerTodas(usuarioCompleto.getId(), 20, 0)
                .stream()
                .map(NotificacionDTO::new)
                .collect(Collectors.toList());
    }
}

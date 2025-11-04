package com.tallerwebi.presentacion;

import java.io.IOException;
import java.util.Base64;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.excepcion.UsuarioInexistenteException;
import com.tallerwebi.dominio.servicios.ServicioPerfil;
import com.tallerwebi.dominio.servicios.ServicioUsuario;
import com.tallerwebi.presentacion.dto.UsuarioSesionDto;

@Controller
@RequestMapping("/usuarios")
public class ControladorPerfil {
    private ServicioUsuario servicioUsuario;
    private ServicioPerfil servicioPerfil;

    @Autowired
    public ControladorPerfil(ServicioUsuario servicioUsuario, ServicioPerfil servicioPerfil) {
        this.servicioUsuario = servicioUsuario;
        this.servicioPerfil = servicioPerfil;
    }

    @GetMapping("/perfil-usuario")
    public ModelAndView mostrarPerfil(HttpServletRequest httpServletRequest) throws UsuarioInexistenteException {
        UsuarioSesionDto usuarioSesion = (UsuarioSesionDto) httpServletRequest.getSession()
                .getAttribute("usuarioLogueado");

        ModelMap modelMap = new ModelMap();
        if (usuarioSesion == null) {
            return new ModelAndView("redirect:/login", modelMap);
        }

        // Recuperar el usuario completo desde la base

        Usuario usuarioCompleto = servicioUsuario.buscarPorId(usuarioSesion.getId());

        // Si no hay foto poner una default

        if (usuarioCompleto.getFotoPerfil() != null) {
            String base64Image = Base64.getEncoder().encodeToString(usuarioCompleto.getFotoPerfil());
            modelMap.put("fotoPerfil", "data:image/png;base64," + base64Image);
        } else {
            modelMap.put("fotoPerfil", httpServletRequest.getContextPath() + "/img/default-profile.png");

        }
        modelMap.put("usuario", usuarioCompleto);

        return new ModelAndView("perfil-usuario", modelMap);
    }

    @PostMapping("/cambiar-foto")
    public ModelAndView cambiarFoto(HttpServletRequest request,
            @RequestParam("foto") MultipartFile archivo) throws IOException, UsuarioInexistenteException {
        UsuarioSesionDto usuarioSesion = (UsuarioSesionDto) request.getSession().getAttribute("usuarioLogueado");
        ModelMap modelMap = new ModelMap();
        if (usuarioSesion == null) {
            return new ModelAndView("redirect:/login");
        }

        // Buscar usuario en base de datos
        Usuario usuario = servicioUsuario.buscarPorId(usuarioSesion.getId());
        boolean exito = servicioPerfil.cambiarFotoPerfil(archivo, usuario);

        if (exito) {
            return new ModelAndView("redirect:/usuarios/perfil-usuario");
        } else if (!exito) {
            modelMap.put("error", "A ocurrido un error");
            return new ModelAndView("perfil-usuario", modelMap);
        }

        return new ModelAndView("redirect:/usuarios/perfil-usuario");
    }

    @PostMapping("/eliminar")
    public ModelAndView eliminarPerfil(HttpServletRequest httpServletRequest) throws UsuarioInexistenteException {
        ModelMap modelMap = new ModelMap();

        UsuarioSesionDto usuarioSesion = (UsuarioSesionDto) httpServletRequest.getSession()
                .getAttribute("usuarioLogueado");

        Usuario usuarioCompleto = servicioUsuario.buscarPorId(usuarioSesion.getId());

        servicioUsuario.eliminarUsuario(usuarioCompleto);

        return new ModelAndView("redirect:/login", modelMap);
    }

    @GetMapping("/editar")
    public ModelAndView editarPerfil(HttpServletRequest httpServletRequest)
            throws UsuarioInexistenteException {
        ModelMap modelMap = new ModelMap();

        UsuarioSesionDto usuarioSesion = (UsuarioSesionDto) httpServletRequest.getSession()
                .getAttribute("usuarioLogueado");

        if (usuarioSesion == null) {
            return new ModelAndView("redirect:/login");
        }

        Usuario usuarioCompleto = servicioUsuario.buscarPorId(usuarioSesion.getId());
        modelMap.addAttribute("usuario", usuarioCompleto);
        return new ModelAndView("editar-perfil", modelMap);
    }

    @PostMapping("/editar")
    public ModelAndView actualizarPerfil(@RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String nombreUsuario,
            @RequestParam(required = false) String telefono,
            @RequestParam(required = false) String direccion,
            HttpServletRequest httpServletRequest) throws UsuarioInexistenteException {

        UsuarioSesionDto usuarioSesion = (UsuarioSesionDto) httpServletRequest.getSession()
                .getAttribute("usuarioLogueado");

        Usuario usuarioActual = servicioUsuario.buscarPorId(usuarioSesion.getId());

        servicioPerfil.actualizarPerfil(nombre, apellido,
                nombreUsuario, direccion, telefono,
                usuarioActual);

        httpServletRequest.setAttribute("usuarioLogueado", usuarioActual);

        return new ModelAndView("redirect:/usuarios/perfil-usuario");
    }

}

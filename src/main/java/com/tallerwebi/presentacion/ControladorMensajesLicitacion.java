package com.tallerwebi.presentacion;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.tallerwebi.dominio.entidades.Cliente;
import com.tallerwebi.dominio.entidades.Comentario;
import com.tallerwebi.dominio.entidades.Licitacion;
import com.tallerwebi.dominio.entidades.Proveedor;
import com.tallerwebi.dominio.servicios.ServicioComentario; // usar interfaz para evitar problemas de proxy JDK
import com.tallerwebi.dominio.servicios.ServicioLicitacion;
import com.tallerwebi.presentacion.dto.UsuarioSesionDto;

@Controller
public class ControladorMensajesLicitacion {

    private final ServicioLicitacion servicioLicitacion;
    private final ServicioComentario servicioComentario; // interfaz extendida con métodos para licitacion

    public ControladorMensajesLicitacion(ServicioLicitacion servicioLicitacion, ServicioComentario servicioComentario) {
        this.servicioLicitacion = servicioLicitacion;
        this.servicioComentario = servicioComentario;
    }

    @GetMapping("/licitacion/{id}/mensajes")
    public ModelAndView verMensajes(@PathVariable("id") Long idLicitacion, HttpServletRequest request) {
        ModelMap modelo = new ModelMap();
        Licitacion licitacion = servicioLicitacion.obtenerPorId(idLicitacion);
        if (licitacion == null) {
            return new ModelAndView("redirect:/dashboard?error=licitacion_inexistente");
        }
        UsuarioSesionDto usuarioSesion = (UsuarioSesionDto) request.getSession().getAttribute("usuarioLogueado");
        if (usuarioSesion != null) {
            if ("CLIENTE".equalsIgnoreCase(usuarioSesion.getRol())) {
                servicioComentario.marcarLeidosParaClienteLicitacion(idLicitacion);
            } else if ("PROVEEDOR".equalsIgnoreCase(usuarioSesion.getRol())) {
                servicioComentario.marcarLeidosParaProveedorLicitacion(idLicitacion);
            }
        }
        modelo.put("licitacion", licitacion);
        modelo.put("comentarios", servicioComentario.obtenerComentarioPorIdLicitacion(idLicitacion));
        modelo.put("usuarioSesion", usuarioSesion);
        return new ModelAndView("licitacion-mensajes", modelo);
    }

    @PostMapping("/licitacion/{id}/mensajes")
    public String crearMensaje(@PathVariable("id") Long idLicitacion,
            @RequestParam("mensaje") String mensaje,
            HttpServletRequest request) {
        if (mensaje == null || mensaje.isBlank()) {
            return "redirect:/licitacion/" + idLicitacion + "/mensajes?error=mensaje_vacio";
        }
        Licitacion licitacion = null;
        licitacion = servicioLicitacion.obtenerPorId(idLicitacion);
        if (licitacion == null) {
            return "redirect:/licitacion/" + idLicitacion + "/mensajes?error=licitacion_inexistente";
        }
        UsuarioSesionDto sesion = (UsuarioSesionDto) request.getSession().getAttribute("usuarioLogueado");
        if (sesion == null) {
            return "redirect:/licitacion/" + idLicitacion + "/mensajes?error=no_autenticado";
        }
        Comentario c = new Comentario();
        c.setLicitacion(licitacion);
        c.setMensaje(mensaje.trim());
        if ("CLIENTE".equalsIgnoreCase(sesion.getRol())) {
            if (licitacion.getCliente() instanceof Cliente) {
                c.setCliente((Cliente) licitacion.getCliente());
            }
        } else if ("PROVEEDOR".equalsIgnoreCase(sesion.getRol())) {
            if (licitacion.getProveedor() instanceof Proveedor) {
                c.setProveedor((Proveedor) licitacion.getProveedor());
            }
        } else {
            return "redirect:/licitacion/" + idLicitacion + "/mensajes?error=rol_invalido";
        }
        servicioComentario.crearComentario(c);
        return "redirect:/licitacion/" + idLicitacion + "/mensajes#ultimo";
    }
}

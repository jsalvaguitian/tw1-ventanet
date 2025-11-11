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
import com.tallerwebi.dominio.entidades.Cotizacion;
import com.tallerwebi.dominio.entidades.Proveedor;
import com.tallerwebi.dominio.servicios.ServicioComentario;
import com.tallerwebi.dominio.servicios.ServicioCotizacion;
import com.tallerwebi.dominio.excepcion.NoHayCotizacionExistente;
import com.tallerwebi.presentacion.dto.UsuarioSesionDto;

@Controller
public class ControladorMensajesCotizacion {

    private final ServicioCotizacion servicioCotizacion;
    private final ServicioComentario servicioComentario;

    public ControladorMensajesCotizacion(ServicioCotizacion servicioCotizacion, ServicioComentario servicioComentario) {
        this.servicioCotizacion = servicioCotizacion;
        this.servicioComentario = servicioComentario;
    }

    @GetMapping("/cotizacion/{id}/mensajes")
    public ModelAndView verMensajes(@PathVariable("id") Long idCotizacion, HttpServletRequest request) throws NoHayCotizacionExistente {
        ModelMap modelo = new ModelMap();

        Cotizacion cotizacion = servicioCotizacion.obtenerPorId(idCotizacion);
        if (cotizacion == null) {
            throw new NoHayCotizacionExistente("No se encontró la cotización especificada.");
        }

        UsuarioSesionDto usuarioSesion = (UsuarioSesionDto) request.getSession().getAttribute("usuarioLogueado");
        
        // Marcar mensajes como leídos según rol
        if (usuarioSesion != null) {
            if ("CLIENTE".equalsIgnoreCase(usuarioSesion.getRol())) {
                servicioComentario.marcarLeidosParaCliente(idCotizacion);
            } else if ("PROVEEDOR".equalsIgnoreCase(usuarioSesion.getRol())) {
                servicioComentario.marcarLeidosParaProveedor(idCotizacion);
            }
        }
        modelo.put("cotizacion", cotizacion);
        modelo.put("comentarios", servicioComentario.obtenerComentarioPorIdCotizacion(idCotizacion));
        modelo.put("usuarioSesion", usuarioSesion);
        return new ModelAndView("cotizacion-mensajes", modelo);
    }

    @PostMapping("/cotizacion/{id}/mensajes")
    public String crearMensaje(@PathVariable("id") Long idCotizacion,
            @RequestParam("mensaje") String mensaje,
            HttpServletRequest request) {
        if (mensaje == null || mensaje.isBlank()) {
            return "redirect:/cotizacion/" + idCotizacion + "/mensajes?error=mensaje_vacio";
        }
        Cotizacion cotizacion = null;
        try {
            cotizacion = servicioCotizacion.obtenerPorId(idCotizacion);
        } catch (NoHayCotizacionExistente e) {
            return "redirect:/cotizacion/" + idCotizacion + "/mensajes?error=cotizacion_inexistente";
        }
        if (cotizacion == null) {
            return "redirect:/cotizacion/" + idCotizacion + "/mensajes?error=cotizacion_inexistente";
        }
        UsuarioSesionDto sesion = (UsuarioSesionDto) request.getSession().getAttribute("usuarioLogueado");
        if (sesion == null) {
            return "redirect:/cotizacion/" + idCotizacion + "/mensajes?error=no_autenticado";
        }

        Comentario c = new Comentario();
        c.setCotizacion(cotizacion);
        c.setMensaje(mensaje.trim());

        // Determinar lado emisor según rol en sesión
        if ("CLIENTE".equalsIgnoreCase(sesion.getRol())) {
            if (cotizacion.getCliente() instanceof Cliente) {
                c.setCliente((Cliente) cotizacion.getCliente());
            }
        } else if ("PROVEEDOR".equalsIgnoreCase(sesion.getRol())) {
            if (cotizacion.getProveedor() instanceof Proveedor) {
                c.setProveedor((Proveedor) cotizacion.getProveedor());
            }
        } else {
            return "redirect:/cotizacion/" + idCotizacion + "/mensajes?error=rol_invalido";
        }

        servicioComentario.crearComentario(c);
        return "redirect:/cotizacion/" + idCotizacion + "/mensajes#ultimo";
    }
}

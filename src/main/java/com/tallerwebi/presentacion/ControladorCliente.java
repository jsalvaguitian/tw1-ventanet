package com.tallerwebi.presentacion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.tallerwebi.dominio.entidades.Cotizacion;
import com.tallerwebi.dominio.enums.EstadoCotizacion;
import com.tallerwebi.dominio.excepcion.NoHayProductoExistente;
import com.tallerwebi.dominio.servicios.ServicioComentario;
import com.tallerwebi.dominio.servicios.ServicioCotizacion;
import com.tallerwebi.presentacion.dto.UsuarioSesionDto;

@Controller
@RequestMapping("/cliente")
public class ControladorCliente {

    // Eliminado ServicioClienteI y ServicioPresupuesto por no uso en el dashboard actual
    private final ServicioCotizacion servicioCotizacion;
    private final ServicioComentario servicioComentario;

    // Constructor principal que Spring debe usar para la inyección
    @Autowired
    public ControladorCliente(ServicioCotizacion servicioCotizacion,
                              ServicioComentario servicioComentario) {
        this.servicioCotizacion = servicioCotizacion;
        this.servicioComentario = servicioComentario;
    }

    // Constructor de compatibilidad con tests antiguos que esperaban ServicioClienteI y ServicioPresupuesto
    public ControladorCliente(com.tallerwebi.dominio.servicios.ServicioClienteI servicioClienteI,
                              com.tallerwebi.dominio.servicios.ServicioPresupuesto servicioPresupuesto,
                              ServicioCotizacion servicioCotizacion,
                              ServicioComentario servicioComentario) {
        this(servicioCotizacion, servicioComentario); // reutiliza el constructor principal
    }

    // Constructor de compatibilidad adicional (tests que pasan sólo ServicioClienteI, ServicioPresupuesto y ServicioCotizacion)
    public ControladorCliente(com.tallerwebi.dominio.servicios.ServicioClienteI servicioClienteI,
                               com.tallerwebi.dominio.servicios.ServicioPresupuesto servicioPresupuesto,
                               ServicioCotizacion servicioCotizacion) {
        this(servicioCotizacion, null); // servicioComentario opcional
    }

    @GetMapping("/dashboard")
    public ModelAndView irDashboard(HttpServletRequest request) {
        ModelMap datosModelado = new ModelMap();

        UsuarioSesionDto usuarioSesion = (UsuarioSesionDto) request.getSession().getAttribute("usuarioLogueado");
        String rol_cliente = "CLIENTE";

        if (usuarioSesion == null || !rol_cliente.equalsIgnoreCase(usuarioSesion.getRol())
                || usuarioSesion.getUsername() == null) {
            return new ModelAndView("redirect:/login");
        }

        System.out.println(
                "[ControladorCliente] usuarioSesion id=" + usuarioSesion.getId() + " rol=" + usuarioSesion.getRol());
        datosModelado.put("nombreCliente", usuarioSesion.getNombre());
        datosModelado.put("apellidoCliente", usuarioSesion.getApellido());
        datosModelado.put("rolCliente", usuarioSesion.getRol());

        try {
            List<Cotizacion> todasLasCotizaciones = servicioCotizacion
                    .obtenerCotizacionPorIdCliente(usuarioSesion.getId());            

            if (todasLasCotizaciones == null) {
                todasLasCotizaciones = new ArrayList<>();
            }

            long totalCotizaciones = todasLasCotizaciones.size();
            long cotizacionesPendientes = todasLasCotizaciones.stream()
                    .filter(c -> c.getEstado() == EstadoCotizacion.PENDIENTE)
                    .count();
            long cotizacionesAprobadas = todasLasCotizaciones.stream()
                    .filter(c -> c.getEstado() == EstadoCotizacion.APROBADA)
                    .count();
            long cotizacionesRechazadas = todasLasCotizaciones.stream()
                    .filter(c -> c.getEstado() == EstadoCotizacion.RECHAZADO)
                    .count();
            long cotizacionesCompletadas = todasLasCotizaciones.stream()
                    .filter(c -> c.getEstado() == EstadoCotizacion.COMPLETADA)
                    .count();

            datosModelado.put("totalCotizaciones", totalCotizaciones);
            datosModelado.put("cotizacionesPendientes", cotizacionesPendientes);
            datosModelado.put("cotizacionesAprobadas", cotizacionesAprobadas);
            datosModelado.put("cotizacionesRechazadas", cotizacionesRechazadas);
            datosModelado.put("cotizacionesCompletadas", cotizacionesCompletadas);
            datosModelado.put("cotizaciones", todasLasCotizaciones);

            // Map de contador de comentarios no leídos para cada cotización (cliente)
            Map<Long, Long> unreadCounts = new HashMap<>();
            for (Cotizacion c : todasLasCotizaciones) {
                if (c.getId() != null) {
                    long noLeidos = 0L;
                    if (servicioComentario != null) {
                        try {
                            noLeidos = servicioComentario.contarNoLeidosParaCliente(c.getId());
                        } catch (Exception ex) {
                            noLeidos = 0L; // tolerante
                        }
                    } else {
                        noLeidos = 0L;
                    }
                    unreadCounts.put(c.getId(), noLeidos);
                }
            }
            datosModelado.put("unreadComentarioCounts", unreadCounts);
            
        } catch (NoHayProductoExistente e) {
            datosModelado.put("cotizaciones", new ArrayList<>());
            datosModelado.put("totalCotizaciones", new ArrayList<>());
            datosModelado.put("cotizacionesPendientes", new ArrayList<>());
            datosModelado.put("cotizacionesAprobadas", new ArrayList<>());
            datosModelado.put("cotizacionesRechazadas", new ArrayList<>());
            datosModelado.put("cotizacionesCompletadas", new ArrayList<>());            
            datosModelado.put("error", "No hay presupuestos disponibles");
        }

        return new ModelAndView("dashboard", datosModelado);
    }
}

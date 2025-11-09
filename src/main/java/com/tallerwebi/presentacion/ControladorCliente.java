package com.tallerwebi.presentacion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.tallerwebi.dominio.entidades.Cotizacion;
import com.tallerwebi.dominio.entidades.Presupuesto;
import com.tallerwebi.dominio.enums.EstadoCotizacion;
import com.tallerwebi.dominio.excepcion.NoHayProductoExistente;
import com.tallerwebi.dominio.servicios.ServicioClienteI;
import com.tallerwebi.dominio.servicios.ServicioCotizacion;
import com.tallerwebi.dominio.servicios.ServicioPresupuesto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tallerwebi.presentacion.dto.UsuarioSesionDto;

@Controller
@RequestMapping("/cliente")
public class ControladorCliente {

    private final ServicioClienteI servicioClienteI;
    private final ServicioPresupuesto servicioPresupuesto; // inyección correcta
    private ServicioCotizacion servicioCotizacion;

    // Constructor que Spring usa para inyección
    public ControladorCliente(ServicioClienteI servicioClienteI, ServicioPresupuesto servicioPresupuesto,
            ServicioCotizacion servicioCotizacion) {
        this.servicioClienteI = servicioClienteI;
        this.servicioPresupuesto = servicioPresupuesto;
        this.servicioCotizacion = servicioCotizacion;
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

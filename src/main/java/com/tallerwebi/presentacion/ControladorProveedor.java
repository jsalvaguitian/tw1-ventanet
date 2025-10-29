package com.tallerwebi.presentacion;

import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.tallerwebi.dominio.entidades.Cotizacion;
import com.tallerwebi.dominio.entidades.Proveedor;
import com.tallerwebi.dominio.enums.EstadoCotizacion;
import com.tallerwebi.dominio.servicios.ServicioCotizacion;
import com.tallerwebi.dominio.servicios.ServicioProveedorI;
import com.tallerwebi.presentacion.dto.UsuarioSesionDto;

@Controller
// @RequestMapping("/proveedor")
public class ControladorProveedor {

    private ServicioProveedorI servicioProveedorI;
    private ServicioCotizacion servicioCotizacion;

    public ControladorProveedor(ServicioProveedorI servicioProveedorI, ServicioCotizacion servicioCotizacion) {
        this.servicioProveedorI = servicioProveedorI;
        this.servicioCotizacion = servicioCotizacion;
    }

    @GetMapping("/dashboard-proveedor")
    public ModelAndView irDashboard(HttpServletRequest request) {
        ModelMap datosModelado = new ModelMap();

        UsuarioSesionDto usuarioSesion = (UsuarioSesionDto) request.getSession().getAttribute("usuarioLogueado");
        String rol_proveedor = "PROVEEDOR";

        if (usuarioSesion == null || !rol_proveedor.equalsIgnoreCase(usuarioSesion.getRol())) {
            return new ModelAndView("redirect:/login");
        }

        List<Cotizacion> todasLasCotizaciones = servicioCotizacion.obtenerPorIdProveedor(usuarioSesion.getId());

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

        datosModelado.put("mailProveedor", usuarioSesion.getUsername());
        return new ModelAndView("dashboard-proveedor", datosModelado);
    }

}

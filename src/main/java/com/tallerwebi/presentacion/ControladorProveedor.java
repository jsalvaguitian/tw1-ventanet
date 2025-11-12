package com.tallerwebi.presentacion;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.tallerwebi.dominio.entidades.Cotizacion;
import com.tallerwebi.dominio.enums.EstadoCotizacion;
import com.tallerwebi.dominio.enums.Rubro;
import com.tallerwebi.dominio.servicios.ServicioCotizacion;
import com.tallerwebi.dominio.servicios.ServicioComentario;
import com.tallerwebi.dominio.servicios.ServicioProveedorI;
import com.tallerwebi.presentacion.dto.UsuarioProvDTO;
import com.tallerwebi.presentacion.dto.UsuarioSesionDto;

@Controller
@RequestMapping("/proveedor")
public class ControladorProveedor {

    // Campos inyectados por Spring (field injection para evitar conflicto de múltiples constructores)
    @Autowired(required = false)
    private ServicioProveedorI servicioProveedorI;
    @Autowired
    private ServicioCotizacion servicioCotizacion;
    @Autowired(required = false)
    private ServicioComentario servicioComentario;

    // Constructor por defecto requerido por algunos mecanismos de instanciación (Jetty/Spring con múltiples constructores previos)
    public ControladorProveedor() {
    }

    // Constructor de compatibilidad para tests antiguos que crean manualmente el controlador
    public ControladorProveedor(ServicioProveedorI servicioProveedorI, ServicioCotizacion servicioCotizacion) {
        this.servicioProveedorI = servicioProveedorI;
        this.servicioCotizacion = servicioCotizacion;
    }

    // Constructor de compatibilidad extendido para tests que incluyen comentario
    public ControladorProveedor(ServicioProveedorI servicioProveedorI, ServicioCotizacion servicioCotizacion, ServicioComentario servicioComentario) {
        this.servicioProveedorI = servicioProveedorI;
        this.servicioCotizacion = servicioCotizacion;
        this.servicioComentario = servicioComentario;
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

        // Map con contador de comentarios no leídos para cada cotización (proveedor)
        Map<Long, Long> unreadCounts = new HashMap<>();
        for (Cotizacion c : todasLasCotizaciones) {
            if (c.getId() != null) {
                long noLeidos = 0L;
                try {
                    noLeidos = servicioComentario.contarNoLeidosParaProveedor(c.getId());
                } catch (Exception ex) {
                    noLeidos = 0L; // tolerante ante errores
                }
                unreadCounts.put(c.getId(), noLeidos);
            }
        }
        datosModelado.put("unreadComentarioCounts", unreadCounts);

        datosModelado.put("mailProveedor", usuarioSesion.getUsername());
        return new ModelAndView("dashboard-proveedor", datosModelado);
    }

    @GetMapping("/filtrar/{estado}")
    @ResponseBody
    public List<UsuarioProvDTO> filtrarProveedoresPorRubro(@PathVariable Boolean estado) {
        List<Proveedor> proveedores = servicioProveedorI.obtenerProveedoresPorEstadoActivoInactivo(estado);
        List<UsuarioProvDTO> provDTOs = convertirProveedoresADtosFiltro(proveedores);

        return provDTOs;
    }

    private List<UsuarioProvDTO> convertirProveedoresADtosFiltro(List<Proveedor> proveedores) {
        List<UsuarioProvDTO> usuarioProvDTOs = new ArrayList<>();

        for (Proveedor uno : proveedores) {
            UsuarioProvDTO dtoProv = new UsuarioProvDTO(uno.getId(), uno.getRazonSocial(), uno.getLogoPath(),
                    uno.getRubro());// pido
            // loguito
            usuarioProvDTOs.add(dtoProv);
        }
        return usuarioProvDTOs;
    }
}

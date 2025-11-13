package com.tallerwebi.presentacion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.ManyToOne;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.tallerwebi.dominio.entidades.Cotizacion;
import com.tallerwebi.dominio.entidades.Licitacion;
import com.tallerwebi.dominio.entidades.ProductoCustom;
import com.tallerwebi.dominio.enums.EstadoCotizacion;
import com.tallerwebi.dominio.enums.EstadoLicitacion;
import com.tallerwebi.dominio.excepcion.NoHayLicitacionesExistentes;
import com.tallerwebi.dominio.excepcion.NoHayProductoExistente;
import com.tallerwebi.dominio.servicios.ServicioComentario;
import com.tallerwebi.dominio.servicios.ServicioCotizacion;
import com.tallerwebi.dominio.servicios.ServicioLicitacion;
import com.tallerwebi.presentacion.dto.LicitacionDto;
import com.tallerwebi.presentacion.dto.ProductoCustomDto;
import com.tallerwebi.presentacion.dto.UsuarioProvDTO;
import com.tallerwebi.presentacion.dto.UsuarioSesionDto;

@Controller
@RequestMapping("/cliente")
public class ControladorCliente {

    // Eliminado ServicioClienteI y ServicioPresupuesto por no uso en el dashboard
    // actual
    private final ServicioCotizacion servicioCotizacion;
    private final ServicioComentario servicioComentario;
    private final ServicioLicitacion servicioLicitacion;

    // Constructor principal que Spring debe usar para la inyección
    @Autowired
    public ControladorCliente(ServicioCotizacion servicioCotizacion,
            ServicioComentario servicioComentario,
            ServicioLicitacion servicioLicitacion) {
        this.servicioCotizacion = servicioCotizacion;
        this.servicioComentario = servicioComentario;
        this.servicioLicitacion = servicioLicitacion;
    }

    // Constructor de compatibilidad con tests antiguos que esperaban
    // ServicioClienteI y ServicioPresupuesto
    public ControladorCliente(com.tallerwebi.dominio.servicios.ServicioClienteI servicioClienteI,
            com.tallerwebi.dominio.servicios.ServicioPresupuesto servicioPresupuesto,
            ServicioCotizacion servicioCotizacion,
            ServicioComentario servicioComentario) {
        this(servicioCotizacion, servicioComentario, null); // reutiliza el constructor principal, servicioLicitacion no
                                                            // disponible
    }

    // Constructor de compatibilidad adicional (tests que pasan sólo
    // ServicioClienteI, ServicioPresupuesto y ServicioCotizacion)
    public ControladorCliente(com.tallerwebi.dominio.servicios.ServicioClienteI servicioClienteI,
            com.tallerwebi.dominio.servicios.ServicioPresupuesto servicioPresupuesto,
            ServicioCotizacion servicioCotizacion) {
        this(servicioCotizacion, null, null); // servicioComentario y servicioLicitacion opcionales
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

    @GetMapping("/dashboard-custom")
    public ModelAndView irDashboardCustom(HttpServletRequest request) {
        ModelMap datosModelado = new ModelMap();

        UsuarioSesionDto usuarioSesion = (UsuarioSesionDto) request.getSession().getAttribute("usuarioLogueado");
        String rol_cliente = "CLIENTE";

        if (usuarioSesion == null || !rol_cliente.equalsIgnoreCase(usuarioSesion.getRol())
                || usuarioSesion.getUsername() == null) {
            return new ModelAndView("redirect:/login");
        }

        datosModelado.put("nombreCliente", usuarioSesion.getNombre());
        datosModelado.put("apellidoCliente", usuarioSesion.getApellido());
        datosModelado.put("rolCliente", usuarioSesion.getRol());

        try {
            List<Licitacion> todasLasLicitacionesEntities = servicioLicitacion
                    .obtenerLicitacionesPorIdCliente(usuarioSesion.getId());

            List<LicitacionDto> todasLasLicitaciones = todasLasLicitacionesEntities.stream()
                    .map(this::MapearLicitacionALicitacionDto) 
                    .collect(Collectors.toList());

            if (todasLasLicitaciones == null) {
                todasLasLicitaciones = new ArrayList<>();
            }

            long totalLicitaciones = todasLasLicitaciones.size();
            long cotizacionesPendientes = todasLasLicitaciones.stream()
                    .filter(c -> c.getEstado() == EstadoLicitacion.PENDIENTE)
                    .count();
            long cotizacionesAprobadas = todasLasLicitaciones.stream()
                    .filter(c -> c.getEstado() == EstadoLicitacion.APROBADA)
                    .count();
            long cotizacionesRechazadas = todasLasLicitaciones.stream()
                    .filter(c -> c.getEstado() == EstadoLicitacion.RECHAZADO)
                    .count();
            long cotizacionesCompletadas = todasLasLicitaciones.stream()
                    .filter(c -> c.getEstado() == EstadoLicitacion.COMPLETADA)
                    .count();

            datosModelado.put("totalCotizaciones", totalLicitaciones);
            datosModelado.put("cotizacionesPendientes", cotizacionesPendientes);
            datosModelado.put("cotizacionesAprobadas", cotizacionesAprobadas);
            datosModelado.put("cotizacionesRechazadas", cotizacionesRechazadas);
            datosModelado.put("cotizacionesCompletadas", cotizacionesCompletadas);
            datosModelado.put("cotizaciones", todasLasLicitaciones);

            // Map de contador de comentarios no leídos para cada cotización (cliente)
            Map<Long, Long> unreadCounts = new HashMap<>();
            // for (Cotizacion c : todasLasCotizaciones) {
            // if (c.getId() != null) {
            // long noLeidos = 0L;
            // if (servicioComentario != null) {
            // try {
            // noLeidos = servicioComentario.contarNoLeidosParaCliente(c.getId());
            // } catch (Exception ex) {
            // noLeidos = 0L; // tolerante
            // }
            // } else {
            // noLeidos = 0L;
            // }
            // unreadCounts.put(c.getId(), noLeidos);
            // }
            // }
            datosModelado.put("unreadComentarioCounts", unreadCounts);

        } catch (NoHayLicitacionesExistentes e) {
            datosModelado.put("cotizaciones", new ArrayList<>());
            datosModelado.put("totalCotizaciones", 0);
            datosModelado.put("cotizacionesPendientes", 0);
            datosModelado.put("cotizacionesAprobadas", 0);
            datosModelado.put("cotizacionesRechazadas", 0);
            datosModelado.put("cotizacionesCompletadas", 0);
            datosModelado.put("error", "No hay presupuestos disponibles");
        }

        return new ModelAndView("dashboard", datosModelado);
    }

    private ProductoCustomDto MapearProductoCustomAProductoCustomDto(ProductoCustom productoCustom) {
        ProductoCustomDto dto = new ProductoCustomDto();
        dto.setId(productoCustom.getId());
        dto.setPrecio(productoCustom.getPrecio());
        dto.setDescripcion(productoCustom.getDescripcion());
        dto.setImgCloudinaryID(productoCustom.getImgCloudinaryID());
        dto.setAncho(productoCustom.getAncho());
        dto.setAlto(productoCustom.getAlto());
        dto.setLargo(productoCustom.getLargo());
        dto.setEspesor(productoCustom.getEspesor());
        dto.setColor(productoCustom.getColor());
        dto.setModelo(productoCustom.getModelo());
        dto.setTipoMaterial(productoCustom.getTipoMaterial());
        dto.setAceptaEnvio(productoCustom.getAceptaEnvio());
        dto.setCantidad(productoCustom.getCantidad());

        if (productoCustom.getProveedor() != null) {
            UsuarioProvDTO dtoProv = new UsuarioProvDTO(productoCustom.getProveedor().getId(),
                    productoCustom.getProveedor().getRazonSocial(), productoCustom.getProveedor().getLogoPath(),
                    productoCustom.getProveedor().getRubro());
            dto.setProveedor(dtoProv);
        }

        return dto;
    }

    private LicitacionDto MapearLicitacionALicitacionDto(Licitacion licitacion) {
        LicitacionDto dto = new LicitacionDto();
        dto.setId(licitacion.getId());
        dto.setEstado(licitacion.getEstado());
        dto.setClienteId(licitacion.getCliente().getId());
        if (licitacion.getProveedor() != null) {
            UsuarioProvDTO dtoProv = new UsuarioProvDTO(licitacion.getProveedor().getId(),
                    licitacion.getProveedor().getRazonSocial(), licitacion.getProveedor().getLogoPath(),
                    licitacion.getProveedor().getRubro());
            dto.setProveedor(dtoProv);
        }
        dto.setProductoCustom(
                MapearProductoCustomAProductoCustomDto(licitacion.getProductoCustom()));
        dto.setFechaCreacion(licitacion.getFechaCreacion());
        dto.setFechaExpiracion(licitacion.getFechaExpiracion());
        return dto;
    }
}

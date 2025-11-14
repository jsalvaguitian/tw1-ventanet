package com.tallerwebi.presentacion;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import com.tallerwebi.dominio.entidades.Licitacion;
import com.tallerwebi.dominio.entidades.ProductoCustom;
import com.tallerwebi.dominio.entidades.Proveedor;
import com.tallerwebi.dominio.enums.EstadoCotizacion;
import com.tallerwebi.dominio.enums.EstadoLicitacion;
import com.tallerwebi.dominio.enums.Rubro;
import com.tallerwebi.dominio.excepcion.NoHayLicitacionesExistentes;
import com.tallerwebi.dominio.servicios.ServicioCotizacion;
import com.tallerwebi.dominio.servicios.ServicioLicitacion;
import com.tallerwebi.dominio.servicios.ServicioComentario;
import com.tallerwebi.dominio.servicios.ServicioProveedorI;
import com.tallerwebi.presentacion.dto.LicitacionDto;
import com.tallerwebi.presentacion.dto.ProductoCustomDto;
import com.tallerwebi.presentacion.dto.UsuarioProvDTO;
import com.tallerwebi.presentacion.dto.UsuarioSesionDto;

@Controller
@RequestMapping("/proveedor")
public class ControladorProveedor {

    // Campos inyectados por Spring (field injection para evitar conflicto de
    // múltiples constructores)
    @Autowired(required = false)
    private ServicioProveedorI servicioProveedorI;
    @Autowired
    private ServicioCotizacion servicioCotizacion;
    @Autowired(required = false)
    private ServicioComentario servicioComentario;
    @Autowired(required = false)
    private final ServicioLicitacion servicioLicitacion;

    // Constructor por defecto requerido por algunos mecanismos de instanciación
    // (Jetty/Spring con múltiples constructores previos)
    public ControladorProveedor() {
        this.servicioLicitacion = null;
    }

    // Constructor de compatibilidad para tests antiguos que crean manualmente el
    // controlador
    public ControladorProveedor(ServicioProveedorI servicioProveedorI, ServicioCotizacion servicioCotizacion) {
        this.servicioLicitacion = null;
        this.servicioProveedorI = servicioProveedorI;
        this.servicioCotizacion = servicioCotizacion;
    }

    // Constructor de compatibilidad extendido para tests que incluyen comentario
    public ControladorProveedor(ServicioProveedorI servicioProveedorI, ServicioCotizacion servicioCotizacion,
            ServicioComentario servicioComentario) {
        this.servicioLicitacion = null;
        this.servicioProveedorI = servicioProveedorI;
        this.servicioCotizacion = servicioCotizacion;
        this.servicioComentario = servicioComentario;
    }

    public ControladorProveedor(ServicioProveedorI servicioProveedorI, ServicioCotizacion servicioCotizacion,
            ServicioLicitacion servicioLicitacion) {
        this.servicioLicitacion = servicioLicitacion;
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

    @GetMapping("/dashboard-proveedor-custom")
    public ModelAndView irDashboardCustom(HttpServletRequest request) {
        ModelMap datosModelado = new ModelMap();

        UsuarioSesionDto usuarioSesion = (UsuarioSesionDto) request.getSession().getAttribute("usuarioLogueado");
        String rol_cliente = "PROVEEDOR";

        if (usuarioSesion == null || !rol_cliente.equalsIgnoreCase(usuarioSesion.getRol())
                || usuarioSesion.getUsername() == null) {
            return new ModelAndView("redirect:/login");
        }

        datosModelado.put("nombreCliente", usuarioSesion.getNombre());
        datosModelado.put("apellidoCliente", usuarioSesion.getApellido());
        datosModelado.put("rolCliente", usuarioSesion.getRol());

        try {
            List<Licitacion> todasLasLicitacionesEntities = servicioLicitacion
                    .obtenerLicitacionesPorIdProveedor(usuarioSesion.getId());

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

        if (licitacion.getCliente() != null) {
            UsuarioSesionDto dtoCli = new UsuarioSesionDto(licitacion.getCliente().getId(),
                    licitacion.getCliente().getUsername(), licitacion.getCliente().getRol(),
                    licitacion.getCliente().getNombre(),
                    licitacion.getCliente().getApellido());
            dto.setCliente(dtoCli);
        }

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

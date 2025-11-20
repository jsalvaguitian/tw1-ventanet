package com.tallerwebi.presentacion;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.tallerwebi.dominio.entidades.Cotizacion;
import com.tallerwebi.dominio.entidades.Licitacion;
import com.tallerwebi.dominio.entidades.MedioDePago;
import com.tallerwebi.dominio.entidades.ProductoCustom;
import com.tallerwebi.dominio.entidades.Proveedor;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.enums.EstadoCotizacion;
import com.tallerwebi.dominio.enums.EstadoLicitacion;
import com.tallerwebi.dominio.excepcion.NoHayLicitacionesExistentes;
import com.tallerwebi.dominio.excepcion.UsuarioInexistenteException;
import com.tallerwebi.dominio.servicios.ServicioCotizacion;
import com.tallerwebi.dominio.servicios.ServicioLicitacion;
import com.tallerwebi.dominio.servicios.ServicioMedioDePago;
import com.tallerwebi.dominio.servicios.ServicioComentario;
import com.tallerwebi.dominio.servicios.ServicioProveedorI;
import com.tallerwebi.dominio.servicios.ServicioUsuario;
import com.tallerwebi.presentacion.dto.LicitacionDto;
import com.tallerwebi.presentacion.dto.MedioDePagoDto;
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
    @Autowired
    private ServicioMedioDePago medioDePagoService;
    @Autowired(required = false)
    private final ServicioUsuario servicioUsuario;

    // Constructor por defecto requerido por algunos mecanismos de instanciación
    // (Jetty/Spring con múltiples constructores previos)
    public ControladorProveedor() {
        this.servicioLicitacion = null;
        this.medioDePagoService = null;
        this.servicioUsuario = null;
    }

    // Constructor de compatibilidad para tests antiguos que crean manualmente el
    // controlador
    public ControladorProveedor(ServicioProveedorI servicioProveedorI, ServicioCotizacion servicioCotizacion) {
        this.servicioLicitacion = null;
        this.servicioProveedorI = servicioProveedorI;
        this.servicioCotizacion = servicioCotizacion;
        this.medioDePagoService = null;
        this.servicioUsuario = null;
    }

    // Constructor de compatibilidad extendido para tests que incluyen comentario
    public ControladorProveedor(ServicioProveedorI servicioProveedorI, ServicioCotizacion servicioCotizacion,
            ServicioComentario servicioComentario) {
        this.servicioLicitacion = null;
        this.servicioProveedorI = servicioProveedorI;
        this.servicioCotizacion = servicioCotizacion;
        this.servicioComentario = servicioComentario;
        this.medioDePagoService = null;
        this.servicioUsuario = null;
    }

    public ControladorProveedor(ServicioProveedorI servicioProveedorI, ServicioCotizacion servicioCotizacion,
            ServicioLicitacion servicioLicitacion, ServicioMedioDePago medioDePagoService,
            ServicioUsuario servicioUsuario) {
        this.servicioLicitacion = servicioLicitacion;
        this.servicioProveedorI = servicioProveedorI;
        this.servicioCotizacion = servicioCotizacion;
        this.medioDePagoService = medioDePagoService;
        this.servicioUsuario = servicioUsuario;
    }

    @GetMapping("/dashboard-proveedor")
    public ModelAndView irDashboard(HttpServletRequest request) throws UsuarioInexistenteException {
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
        if (servicioUsuario != null) {
            String base64Image = servicioUsuario.obtenerFotoPerfil(usuarioSesion.getId(), request);
            datosModelado.put("fotoPerfil", base64Image);
        }

        return new ModelAndView("dashboard-proveedor", datosModelado);
    }

    @GetMapping("/dashboard-proveedor-custom")
    public ModelAndView irDashboardCustom(HttpServletRequest request) throws UsuarioInexistenteException {
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

        String base64Image = servicioUsuario.obtenerFotoPerfil(usuarioSesion.getId(), request);
        datosModelado.put("fotoPerfil", base64Image);

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

            // Map de contador de comentarios no leídos para cada licitación (proveedor)
            Map<Long, Long> unreadCounts = new HashMap<>();
            if (servicioComentario != null) {
                for (LicitacionDto l : todasLasLicitaciones) {
                    if (l.getId() != null) {
                        long noLeidos = 0L;
                        try {
                            noLeidos = servicioComentario.contarNoLeidosParaProveedorLicitacion(l.getId());
                        } catch (Exception ex) {
                            noLeidos = 0L;
                        }
                        unreadCounts.put(l.getId(), noLeidos);
                    }
                }
            }
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

    @GetMapping("/estadisticas")
    public ModelAndView mostrarPerfil(HttpServletRequest httpServletRequest) throws UsuarioInexistenteException {
        UsuarioSesionDto usuarioSesion = (UsuarioSesionDto) httpServletRequest.getSession()
                .getAttribute("usuarioLogueado");

        ModelMap modelMap = new ModelMap();
        if (usuarioSesion == null) {
            return new ModelAndView("redirect:/login", modelMap);
        }

        Proveedor proveedor = servicioProveedorI.buscarPorId(usuarioSesion.getId());

        Long proveedorId = proveedor.getId();

        Map<String, Long> estadisticas = servicioCotizacion
                .obtenerEstadisticasCotizacionesDelProveedor(proveedorId);

        Map<String, Object> promedioGeneralComparacion = servicioCotizacion
                .obtenerEstadisticaComparacionEntreProveedores(proveedorId);

        Map<String, Long> productosMasCotizados = servicioCotizacion.obtenerProductosMasCotizados(proveedorId);

        Map<String, Long> productosMasCotizadosDeTodosLosProveedores = servicioCotizacion
                .obtenerProductosMasCotizadosDeTodosLosProveedores();

        boolean sinCotizaciones = true;
        if (estadisticas != null) { // Para mostrar mensaje si no tiene cotizaciones en lugar del grafico
            for (Long v : estadisticas.values()) { // El nesteo es terrible pero no encontre otra forma
                if (v != null && v > 0) {
                    sinCotizaciones = false;
                    break;
                }
            }
        }

        if (sinCotizaciones) {
            modelMap.addAttribute("promedioGeneralComparacion", promedioGeneralComparacion);
            modelMap.addAttribute("productosMasCotizadosDeTodosLosProveedores",
                    productosMasCotizadosDeTodosLosProveedores);
            modelMap.addAttribute("graficoVacio", "No tienes cotizaciones para mostrar estadísticas aún.");
        } else {
            modelMap.addAttribute("productosMasCotizadosDeTodosLosProveedores",
                    productosMasCotizadosDeTodosLosProveedores);
            modelMap.addAttribute("promedioGeneralComparacion", promedioGeneralComparacion);
            modelMap.addAttribute("productosMasCotizados", productosMasCotizados);
            modelMap.addAttribute("estadisticas", estadisticas);
        }

        modelMap.put("usuario", proveedor);
        return new ModelAndView("estadisticas", modelMap);
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

    @GetMapping("/medios-pago")
    public ModelAndView mostrarProveedorMedioPago(HttpServletRequest request) throws UsuarioInexistenteException {
        ModelMap datosModelado = new ModelMap();

        UsuarioSesionDto usuarioSesion = (UsuarioSesionDto) request.getSession().getAttribute("usuarioLogueado");
        String rol_proveedor = "PROVEEDOR";

        if (usuarioSesion == null || !rol_proveedor.equalsIgnoreCase(usuarioSesion.getRol())) {
            return new ModelAndView("redirect:/login");
        }

        List<MedioDePago> mediosProveedorAux = servicioProveedorI.obtenerMediosDePagoDeProveedor(usuarioSesion.getId());

        List<MedioDePagoDto> mediosProveedor = mediosProveedorAux.stream()
                .map(m -> new MedioDePagoDto() {
                    {
                        setId(m.getId());
                        setNombre(m.getNombre());
                        setImagen(m.getImagen());
                        setTipo(m.getTipo());
                    }
                })
                .collect(Collectors.toList());

        List<MedioDePago> mediosDisponibles = medioDePagoService.obtenerTodosLosMedios();
        List<Long> idsMediosProveedor = mediosProveedorAux
                .stream()
                .map(MedioDePago::getId)
                .collect(Collectors.toList());

        if (servicioUsuario != null) {
            String base64Image = servicioUsuario.obtenerFotoPerfil(usuarioSesion.getId(), request);
            datosModelado.put("fotoPerfil", base64Image);
        }
        datosModelado.addAttribute("idsMediosProveedor", idsMediosProveedor);
        datosModelado.put("mailProveedor", usuarioSesion.getUsername());
        datosModelado.addAttribute("mediosProveedor", mediosProveedor);
        datosModelado.addAttribute("mediosDisponibles", mediosDisponibles);

        return new ModelAndView("proveedor-medio-pago", datosModelado);
    }

    @PostMapping("/guardar-medios-pago")
    public ModelAndView guardarMediosPagoProveedor(
            @RequestParam("mediosPagoIds") List<Long> mediosPagoIds,
            HttpServletRequest request) {

        UsuarioSesionDto usuarioSesion = (UsuarioSesionDto) request.getSession().getAttribute("usuarioLogueado");
        String rol_proveedor = "PROVEEDOR";

        if (usuarioSesion == null || !rol_proveedor.equalsIgnoreCase(usuarioSesion.getRol())) {
            return new ModelAndView("redirect:/login");
        }

        try {
            servicioProveedorI.actualizarMediosPago(usuarioSesion.getId(), mediosPagoIds);
            return new ModelAndView("redirect:/proveedor/medios-pago?exito=true");

        } catch (Exception e) {
            return new ModelAndView("redirect:/proveedor/medios-pago?error=true");
        }
    }

    @GetMapping("/medios-pago/{proveedorId}")
    @ResponseBody // Indica a Spring que la respuesta debe ser escrita directamente en el cuerpo
                  // (como JSON)
    public ResponseEntity<List<MedioDePago>> obtenerMediosPagoPorProveedor(@PathVariable Long proveedorId) {

        // 1. Obtener el proveedor.
        List<MedioDePago> mediosPago = servicioProveedorI.obtenerMediosDePagoDeProveedor(proveedorId);

        if (mediosPago == null) {
            mediosPago = new ArrayList<>();
        }

        return ResponseEntity.ok(mediosPago);
    }
}

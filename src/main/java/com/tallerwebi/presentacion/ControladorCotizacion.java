package com.tallerwebi.presentacion;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tallerwebi.dominio.entidades.Cliente;
import com.tallerwebi.dominio.entidades.Cotizacion;
import com.tallerwebi.dominio.entidades.CotizacionItem;
import com.tallerwebi.dominio.entidades.Producto;
import com.tallerwebi.dominio.entidades.Proveedor;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.entidades.MedioDePago;
import com.tallerwebi.dominio.enums.EstadoCotizacion;
import com.tallerwebi.dominio.excepcion.CotizacionesExistente;
import com.tallerwebi.dominio.excepcion.NoHayCotizacionExistente;
import com.tallerwebi.dominio.excepcion.UsuarioInexistenteException;
import com.tallerwebi.dominio.servicios.ServicioClienteI;
import com.tallerwebi.dominio.servicios.ServicioCotizacion;
import com.tallerwebi.dominio.servicios.ServicioProducto;
import com.tallerwebi.dominio.servicios.ServicioProveedorI;
import com.tallerwebi.dominio.servicios.ServicioTablas;
import com.tallerwebi.dominio.servicios.ServicioTipoProducto;
import com.tallerwebi.dominio.servicios.ServicioTipoVentana;
import com.tallerwebi.dominio.servicios.ServicioUsuario;
import com.tallerwebi.dominio.servicios.ServicioMedioDePago;
import com.tallerwebi.presentacion.dto.CotizacionDTO;
import com.tallerwebi.presentacion.dto.CotizacionItemDTO;
import com.tallerwebi.presentacion.dto.UsuarioSesionDto;

@Controller
@RequestMapping("/cotizacion")
public class ControladorCotizacion {
    private final ServicioCotizacion servicioCotizacion;
    private ServicioProducto servicioProducto;
    private ServicioUsuario servicioUsuario;
    private ServicioProveedorI servicioProveedor;
    private final ServicioTipoProducto servicioTipoProducto;
    private final ServicioTipoVentana servicioTipoVentana;
    private final ServicioTablas servicioTablas;
    private final ServicioMedioDePago servicioMedioDePago;

    @Autowired
    public ControladorCotizacion(ServicioTipoProducto servicioTipoProducto, ServicioTipoVentana servicioTipoVentana,
            ServicioTablas servicioTablas, ServicioCotizacion servicioCotizacion, ServicioProducto servicioProducto,
            ServicioUsuario servicioUsuario, ServicioProveedorI servicioProveedor, ServicioMedioDePago servicioMedioDePago) {
        this.servicioCotizacion = servicioCotizacion;
        this.servicioProducto = servicioProducto;
        this.servicioUsuario = servicioUsuario;
        this.servicioProveedor = servicioProveedor;
        this.servicioTipoProducto = servicioTipoProducto;
        this.servicioTipoVentana = servicioTipoVentana;
        this.servicioTablas = servicioTablas;
        this.servicioMedioDePago = servicioMedioDePago;
    }

    @GetMapping("/presupuesto")
    public String mostrarFormulario(Model model, HttpServletRequest request) {

        UsuarioSesionDto usuarioSesion = (UsuarioSesionDto) request.getSession().getAttribute("usuarioLogueado");
        String rol_proveedor = "CLIENTE";

        if (usuarioSesion == null || !rol_proveedor.equalsIgnoreCase(usuarioSesion.getRol())) {
            return "redirect:/login";
        }

        // agregar tipos de producto al modelo para llenar el select
        model.addAttribute("tiposProducto", servicioTipoProducto.obtener());
        model.addAttribute("colores", servicioTablas.obtenerColores());
        model.addAttribute("materiales", servicioTablas.obtenerMateriales());
        model.addAttribute("altos", servicioTablas.obtenerAltos());
        model.addAttribute("anchos", servicioTablas.obtenerAnchos());
        model.addAttribute("provincias", servicioTablas.obtenerProvincias());

        return "presupuesto";
    }

    @GetMapping("/detalle/{id}")
    @ResponseBody
    public ResponseEntity<?> obtenerDetalleCotizacion(@PathVariable Long id) throws NoHayCotizacionExistente {

        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().body("El ID de cotización no es válido");
        }

        Cotizacion cotizacion = servicioCotizacion.obtenerPorId(id);
        if (cotizacion == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cotización no encontrada");
        }
        return ResponseEntity.ok(cotizacion);
    }

    @PostMapping("/{id}/cambiar-estado/{nuevoEstado}")
    @ResponseBody
    public ModelAndView cambiarEstadoCotizacion(@PathVariable Long id, @PathVariable String nuevoEstado,
            HttpServletRequest request)
            throws CotizacionesExistente {

        UsuarioSesionDto usuarioSesionDto = (UsuarioSesionDto) request.getSession().getAttribute("usuarioLogueado");
        String rol_admin = "ADMIN";
        String rol_cliente = "CLIENTE";
        String rol_proveedor = "PROVEEDOR";

        if (nuevoEstado == null || nuevoEstado.isEmpty()) {
            if (usuarioSesionDto == null) {
                return new ModelAndView("redirect:/login");
            } else {
                if (rol_admin.equalsIgnoreCase(usuarioSesionDto.getRol())) {
                    return new ModelAndView("redirect:/admin/dashboard-admin");
                } else if (rol_cliente.equalsIgnoreCase(usuarioSesionDto.getRol())) {
                    return new ModelAndView("redirect:/cliente/dashboard");
                } else if (rol_proveedor.equalsIgnoreCase(usuarioSesionDto.getRol())) {
                    return new ModelAndView("redirect:/proveedor/dashboard-proveedor");
                }
            }
        }
        EstadoCotizacion estado = EstadoCotizacion.valueOf(nuevoEstado);
        servicioCotizacion.actualizarEstado(id, estado);
        if (rol_admin.equalsIgnoreCase(usuarioSesionDto.getRol())) {
            return new ModelAndView("redirect:/admin/dashboard-admin");
        } else if (rol_cliente.equalsIgnoreCase(usuarioSesionDto.getRol())) {
            return new ModelAndView("redirect:/cliente/dashboard");
        } else if (rol_proveedor.equalsIgnoreCase(usuarioSesionDto.getRol())) {
            return new ModelAndView("redirect:/proveedor/dashboard-proveedor");
        }
        return null;
    }

    @PostMapping("/datos-previsualizar")
    @ResponseBody
    public ResponseEntity<List<Cotizacion>> ObtenerDatosPrevisualizar(@RequestBody Map<String, List<Long>> body,
            HttpServletRequest request, RedirectAttributes redirectAttributes) {
        List<Long> productosIds = body.get("productosIds");
        List<Cotizacion> cotizaciones = new ArrayList<>();

        try {
            List<Producto> productoList = servicioProducto.obtenertodosPorListadoId(productosIds);

            if (productoList.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("error", "No se seleccionaron productos válidos");
                return ResponseEntity.badRequest().body(cotizaciones);
            }

            Map<Proveedor, List<Producto>> productosPorProveedor = productoList.stream()
                    .collect(Collectors.groupingBy(Producto::getProveedor));

            UsuarioSesionDto usuarioSesionDto = (UsuarioSesionDto) request.getSession().getAttribute("usuarioLogueado");

            if (usuarioSesionDto == null) {
                ResponseEntity.badRequest().body(cotizaciones);
            }

            Cliente cliente = new Cliente();
            cliente.setId(usuarioSesionDto.getId());
            cliente.setNombre(usuarioSesionDto.getNombre());
            cliente.setApellido(usuarioSesionDto.getApellido());

            for (Map.Entry<Proveedor, List<Producto>> entry : productosPorProveedor.entrySet()) {
                Proveedor proveedor = entry.getKey();
                List<Producto> productosDelProveedor = entry.getValue();

                Cotizacion cotizacion = new Cotizacion();
                cotizacion.setProveedor(proveedor);
                cotizacion.setCliente(cliente);
                cotizacion.setFechaCreacion(LocalDate.now());
                cotizacion.setEstado(EstadoCotizacion.PENDIENTE);

                List<CotizacionItem> items = new ArrayList<>();
                double total = 0;

                for (Producto producto : productosDelProveedor) {
                    CotizacionItem item = new CotizacionItem();
                    item.setProducto(producto);
                    item.setCantidad(1);
                    item.setPrecioUnitario(producto.getPrecio());
                    item.setCotizacion(cotizacion);

                    total += producto.getPrecio();
                    items.add(item);
                }

                cotizacion.setItems(items);
                cotizacion.setMontoTotal(total);
                cotizaciones.add(cotizacion);
            }
            request.getSession().setAttribute("cotizaciones", cotizaciones);

            return ResponseEntity.ok(cotizaciones);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(cotizaciones);
        }
    }

    @GetMapping("/previsualizar")
    public ModelAndView previsualizar(
            HttpServletRequest request) {
        try {
            ModelMap datosModelado = new ModelMap();
            List<Cotizacion> cotizaciones = (List<Cotizacion>) request.getSession()
                    .getAttribute("cotizaciones");
            if (cotizaciones == null || cotizaciones.isEmpty()) {
                // Redirigir si la sesión está vacía (ej. acceso directo o sesión expirada)
                return new ModelAndView("previsualizar-cotizacion", datosModelado);
            }

            request.getSession().removeAttribute("cotizaciones");

            
            datosModelado.put("cotizaciones", cotizaciones);
            datosModelado.put("esConfirmacion", false);

            return new ModelAndView("previsualizar-cotizacion", datosModelado);

        } catch (Exception e) {
            e.printStackTrace();
            ModelMap datosModelado = new ModelMap();            
            return new ModelAndView("previsualizar-cotizacion", datosModelado);
        }
    }

    @PostMapping("/confirmar")
    public ModelAndView confirmarCotizaciones(
            @RequestParam("cotizacionesJson") String cotizacionesJson,
            HttpServletRequest request) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        List<CotizacionDTO> cotizaciones = mapper.readValue(
                cotizacionesJson,
                new com.fasterxml.jackson.core.type.TypeReference<List<CotizacionDTO>>() {
                });

        List<Cotizacion> cotizacionesGuardadas = new ArrayList<>();
        List<String> errores = new ArrayList<>();

        for (CotizacionDTO dto : cotizaciones) {
            if (!dto.isSeleccionada())
                continue;

            try {
                // 🔹 Validar existencia de Cliente
                Usuario cliente = servicioUsuario.buscarPorId(dto.getClienteId());
                if (cliente == null) {
                    errores.add("Cliente no encontrado con ID " + dto.getClienteId());
                    continue;
                }

                // 🔹 Validar existencia de Proveedor
                Proveedor proveedor = servicioProveedor.buscarPorId(dto.getProveedorId());
                if (proveedor == null) {
                    errores.add("Proveedor no encontrado con ID " + dto.getProveedorId());
                    continue;
                }

               

                // 🔹 Crear entidad Cotización
                Cotizacion cot = new Cotizacion();
                cot.setCliente((Cliente) cliente);
                cot.setProveedor(proveedor);
                cot.setMontoTotal(dto.getMontoTotal());
                cot.setEstado(EstadoCotizacion.PENDIENTE);
                cot.setFechaCreacion(LocalDate.now());
                cot.setFechaExpiracion(LocalDate.now().plusDays(7));

                 MedioDePago medioDePago = servicioMedioDePago.obtenerPorId(dto.getMedioPagoId());
                if (medioDePago != null) {
                    cot.setMedioDePago(medioDePago);
                }
                

                List<CotizacionItem> items = new ArrayList<>();

                for (CotizacionItemDTO itemDto : dto.getItems()) {
                    Producto producto = servicioProducto.obtenerPorId(itemDto.getIdProducto());
                    if (producto == null) {
                        errores.add("Producto no encontrado con ID " + itemDto.getIdProducto());
                        continue;
                    }

                    CotizacionItem item = new CotizacionItem();
                    item.setProducto(producto);
                    item.setCantidad(itemDto.getCantidad());
                    item.setPrecioUnitario(producto.getPrecio());
                    item.setCotizacion(cot);
                    items.add(item);
                }

                if (items.isEmpty()) {
                    errores.add("No se pudieron agregar ítems válidos a la cotización del cliente ID "
                            + dto.getClienteId());
                    continue;
                }

                // 🔹 Calcular total
                double total = items.stream()
                        .mapToDouble(i -> i.getPrecioUnitario() * i.getCantidad())
                        .sum();
                cot.setMontoTotal(total);
                cot.setItems(items);

                // 🔹 Guardar
                servicioCotizacion.guardar(cot);
                cotizacionesGuardadas.add(cot);

            } catch (Exception ex) {
                errores.add(
                        "Error al guardar cotización para cliente ID " + dto.getClienteId() + ": " + ex.getMessage());
            }
        }

        // 🔹 Vaciar cotizaciones en sesión para evitar reenvíos
        request.getSession().removeAttribute("cotizaciones");

        // 🔹 Preparar modelo para la vista
        ModelMap modelo = new ModelMap();
        modelo.put("cotizaciones", cotizacionesGuardadas);
        modelo.put("errores", errores);
        modelo.put("esConfirmacion", true);

        if (errores.isEmpty() && !cotizacionesGuardadas.isEmpty()) {
            modelo.put("mensajeExito",
                    "Todas las cotizaciones se confirmaron correctamente. El proveedor se contactará pronto.");
        } else if (!errores.isEmpty() && cotizacionesGuardadas.isEmpty()) {
            modelo.put("mensajeError", "No se pudo guardar ninguna cotización.");
        } else {
            modelo.put("mensajeParcial", "Algunas cotizaciones se guardaron correctamente, pero otras fallaron.");
        }

        return new ModelAndView("previsualizar-cotizacion", modelo);
    }

}

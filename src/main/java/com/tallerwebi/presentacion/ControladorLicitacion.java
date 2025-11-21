package com.tallerwebi.presentacion;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tallerwebi.dominio.entidades.Cliente;
import com.tallerwebi.dominio.entidades.Licitacion;
import com.tallerwebi.dominio.entidades.ProductoCustom;
import com.tallerwebi.dominio.entidades.Proveedor;
import com.tallerwebi.dominio.enums.EstadoLicitacion;
import com.tallerwebi.dominio.enums.Rubro;
import com.tallerwebi.dominio.excepcion.LicitacionesExistente;
import com.tallerwebi.dominio.excepcion.NoHayCotizacionExistente;
import com.tallerwebi.dominio.servicios.ServicioCloudinary;
import com.tallerwebi.dominio.servicios.ServicioLicitacion;
import com.tallerwebi.dominio.servicios.ServicioProductoCustom;
import com.tallerwebi.dominio.servicios.ServicioProveedorI;
import com.tallerwebi.dominio.servicios.ServicioTipoProducto;
import com.tallerwebi.dominio.servicios.ServicioUsuario;
import com.tallerwebi.presentacion.dto.ActualizacionLicitacionDto;
import com.tallerwebi.presentacion.dto.LicitacionDto;
import com.tallerwebi.presentacion.dto.ProductoCustomDto;
import com.tallerwebi.presentacion.dto.UsuarioProvDTO;
import com.tallerwebi.presentacion.dto.UsuarioSesionDto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Controller
@RequestMapping("/licitacion")
public class ControladorLicitacion {
    private ServicioUsuario servicioUsuario;
    private ServicioProveedorI servicioProveedor;
    private ServicioCloudinary servicioCloudinary;
    private ServicioProductoCustom servicioProductoCustom;
    private ServicioLicitacion servicioLicitacion;

    @Autowired
    public ControladorLicitacion(ServicioUsuario servicioUsuario, ServicioProveedorI servicioProveedor,
            ServicioTipoProducto servicioTipoProducto, ServicioCloudinary servicioCloudinary,
            ServicioProductoCustom productoCustomService, ServicioLicitacion servicioLicitacion) {
        this.servicioUsuario = servicioUsuario;
        this.servicioProveedor = servicioProveedor;
        this.servicioCloudinary = servicioCloudinary;
        this.servicioProductoCustom = productoCustomService;
        this.servicioLicitacion = servicioLicitacion;
    }

    @GetMapping("/presupuesto-custom")
    public ModelAndView mostrarFormulario(HttpServletRequest request) {
        ModelMap modelo = new ModelMap();

        UsuarioSesionDto usuarioSesion = (UsuarioSesionDto) request.getSession().getAttribute("usuarioLogueado");
        String rol_proveedor = "CLIENTE";

        if (usuarioSesion == null || !rol_proveedor.equalsIgnoreCase(usuarioSesion.getRol())) {
            return new ModelAndView("redirect:/login");
        }

        List<Rubro> rubros = servicioProveedor.obtenerRubrosActivos();
        modelo.put("rubros", rubros);

        List<Proveedor> proveedores = servicioProveedor.obtenerTodosLosProveedoresActivos();
        List<UsuarioProvDTO> provDTOs = convertirProveedoresADtosFiltro(proveedores);

        modelo.addAttribute("productoCustom", new ProductoCustom());
        // modelo.addAttribute("tiposProducto", servicioTipoProducto.obtener());
        modelo.put("proveedores", provDTOs);

        return new ModelAndView("licitacion", modelo);

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

    @PostMapping("/confirmar")
    public ModelAndView confirmarCotizaciones(
            @RequestParam("licitacionJson") String licitacionJson,
            @RequestParam(value = "imagenFile", required = false) MultipartFile imagenFile,
            HttpServletRequest request) {

        UsuarioSesionDto usuarioSesion = (UsuarioSesionDto) request.getSession().getAttribute("usuarioLogueado");
        String rol_cliente = "CLIENTE";

        // Validación de sesión
        if (usuarioSesion == null || !rol_cliente.equalsIgnoreCase(usuarioSesion.getRol())) {
            return new ModelAndView("redirect:/login");
        }

        Cliente cliente = new Cliente();
        cliente.setId(usuarioSesion.getId());

        ModelAndView mav = new ModelAndView("licitacion"); // vuelve a la misma vista

        try {
            ObjectMapper mapper = new ObjectMapper();
            LicitacionDto dto = mapper.readValue(licitacionJson, LicitacionDto.class);

            // Validaciones
            if (dto.getProveedoresIds() == null || dto.getProveedoresIds().isEmpty()) {

                mav.addObject("productoCustom", dto.getProductoCustom());
                mav.addObject("errorMessage", "Debe seleccionar al menos un proveedor.");
                return mav;
            }

            if (dto.getProductoCustom() == null || dto.getProductoCustom().getDescripcion() == null ||
                    dto.getProductoCustom().getDescripcion().trim().isEmpty()) {
                mav.addObject("productoCustom", dto.getProductoCustom());
                mav.addObject("errorMessage", "Debe completar la descripción del producto.");
                return mav;
            }

            // Imagen
            String imagenUrl = null;
            if (imagenFile != null && !imagenFile.isEmpty()) {
                Map<String, Object> resultado = servicioCloudinary.subirImagen(imagenFile);
                imagenUrl = (String) resultado.get("url");
            }

            // Crear licitaciones
            for (Long proveedorId : dto.getProveedoresIds()) {
                Proveedor proveedor = servicioProveedor.buscarPorId(proveedorId);
                if (proveedor == null)
                    continue;

                ProductoCustom producto = new ProductoCustom();
                ProductoCustomDto pDto = dto.getProductoCustom();
                producto.setDescripcion(pDto.getDescripcion());
                producto.setAncho(pDto.getAncho());
                producto.setAlto(pDto.getAlto());
                producto.setLargo(pDto.getLargo());
                producto.setEspesor(pDto.getEspesor());
                producto.setTipoMaterial(pDto.getTipoMaterial());
                producto.setColor(pDto.getColor());
                producto.setCantidad(pDto.getCantidad());
                producto.setImgCloudinaryID(imagenUrl);
                producto.setProveedor(proveedor);

                servicioProductoCustom.crearProducto(producto);

                Licitacion lic = new Licitacion();
                lic.setProductoCustom(producto);
                lic.setProveedor(proveedor);
                lic.setCliente(cliente);
                lic.setEstado(EstadoLicitacion.PENDIENTE);
                lic.setMontoTotal(dto.getMontoTotal() != null ? dto.getMontoTotal() : 0.0);
                lic.setFechaCreacion(LocalDate.now());
                lic.setFechaExpiracion(LocalDate.now().plusDays(7));

                servicioLicitacion.crear(lic);
            }

            // mensaje de éxito
            mav.addObject("productoCustom", dto.getProductoCustom());
            mav.addObject("successMessage", "¡Licitación creada con éxito!");

        } catch (Exception ex) {
            ex.printStackTrace();
            mav.addObject("productoCustom", new ProductoCustomDto());
            mav.addObject("errorMessage", "Ocurrió un error al procesar la licitación.");
        }

        return mav;
    }

    @GetMapping("/detalle/{id}")
    @ResponseBody
    public ResponseEntity<?> obtenerDetalleCotizacion(@PathVariable Long id) throws NoHayCotizacionExistente {

        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().body("El ID de licitacion no es válido");
        }

        Licitacion licitacion = servicioLicitacion.obtenerPorId(id);
        if (licitacion == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Licitacion no encontrada");
        }
        return ResponseEntity.ok(licitacion);
    }

    @PostMapping("/{id}/cambiar-estado/{nuevoEstado}")
    @ResponseBody
    public ModelAndView cambiarEstadoCotizacion(@PathVariable Long id, @PathVariable String nuevoEstado,
            HttpServletRequest request)
            throws LicitacionesExistente {

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

        EstadoLicitacion estado = EstadoLicitacion.valueOf(nuevoEstado);

        servicioLicitacion.actualizarEstado(id, estado);
        if (rol_admin.equalsIgnoreCase(usuarioSesionDto.getRol())) {
            return new ModelAndView("redirect:/admin/dashboard-admin");
        } else if (rol_cliente.equalsIgnoreCase(usuarioSesionDto.getRol())) {
            return new ModelAndView("redirect:/cliente/dashboard");
        } else if (rol_proveedor.equalsIgnoreCase(usuarioSesionDto.getRol())) {
            return new ModelAndView("redirect:/proveedor/dashboard-proveedor");
        }
        return null;
    }

    @PostMapping("/{id}/cambiar-estado")
    @ResponseBody
    public ModelAndView cambiarEstadoCotizacionYValor(@PathVariable Long id,
            @RequestBody ActualizacionLicitacionDto dto,
            HttpServletRequest request)
            throws LicitacionesExistente {

        UsuarioSesionDto usuarioSesionDto = (UsuarioSesionDto) request.getSession().getAttribute("usuarioLogueado");
        String rol_admin = "ADMIN";
        String rol_cliente = "CLIENTE";
        String rol_proveedor = "PROVEEDOR";

        if (dto.getNuevoEstado() == null || dto.getNuevoEstado().isEmpty()) {
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

        EstadoLicitacion estado = EstadoLicitacion.valueOf(dto.getNuevoEstado());

        servicioLicitacion.actualizarEstadoYPrecioUnitario(id, estado, dto.getPrecioUnitario());
        if (rol_admin.equalsIgnoreCase(usuarioSesionDto.getRol())) {
            return new ModelAndView("redirect:/admin/dashboard-admin");
        } else if (rol_cliente.equalsIgnoreCase(usuarioSesionDto.getRol())) {
            return new ModelAndView("redirect:/cliente/dashboard");
        } else if (rol_proveedor.equalsIgnoreCase(usuarioSesionDto.getRol())) {
            return new ModelAndView("redirect:/proveedor/dashboard-proveedor");
        }
        return null;
    }
}

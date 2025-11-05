package com.tallerwebi.presentacion;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tallerwebi.dominio.entidades.Cliente;
import com.tallerwebi.dominio.entidades.Cotizacion;
import com.tallerwebi.dominio.entidades.CotizacionItem;
import com.tallerwebi.dominio.entidades.Producto;
import com.tallerwebi.dominio.entidades.Proveedor;
import com.tallerwebi.dominio.enums.EstadoCotizacion;
import com.tallerwebi.dominio.excepcion.CotizacionesExistente;
import com.tallerwebi.dominio.servicios.ServicioCotizacion;
import com.tallerwebi.dominio.servicios.ServicioProducto;

@Controller
@RequestMapping("/cotizacion")
public class ControladorCotizacion {
    private final ServicioCotizacion servicioCotizacion;
    private ServicioProducto servicioProducto;

    @Autowired
    public ControladorCotizacion(ServicioCotizacion servicioCotizacion, ServicioProducto servicioProducto) {
        this.servicioCotizacion = servicioCotizacion;
        this.servicioProducto = servicioProducto;
    }

    @GetMapping("/detalle/{id}")
    @ResponseBody
    public Cotizacion obtenerDetalleCotizacion(@PathVariable Long id) {
        return servicioCotizacion.obtenerPorId(id);
    }

    @PostMapping("/{id}/cambiar-estado/{nuevoEstado}")
    @ResponseBody
    public ModelAndView cambiarEstadoCotizacion(@PathVariable Long id, @PathVariable String nuevoEstado)
            throws CotizacionesExistente {

        if (nuevoEstado == null || nuevoEstado.isEmpty()) {
            return new ModelAndView("redirect:/proveedor/dashboard-proveedor");
        }
        EstadoCotizacion estado = EstadoCotizacion.valueOf(nuevoEstado);
        servicioCotizacion.actualizarEstado(id, estado);
        return new ModelAndView("redirect:/proveedor/dashboard-proveedor");
    }

    // @PostMapping("/previsualizar")
    // public ModelAndView crearPrevisualizacionOld(@RequestBody List<Long>
    // productosIds,
    // HttpServletRequest request) {

    // ModelMap datosModelado = new ModelMap();

    // // Obtener productos desde el servicio
    // List<Producto> productoList =
    // servicioProducto.obtenertodosPorListadoId(productosIds);

    // if (productoList.isEmpty()) {
    // datosModelado.addAttribute("mensaje", "No se seleccionaron productos
    // válidos");
    // return new ModelAndView("previsualizar-cotizacion", datosModelado);
    // }

    // // Agrupar productos por proveedor
    // Map<Proveedor, List<Producto>> productosPorProveedor = productoList.stream()
    // .collect(Collectors.groupingBy(Producto::getProveedor));

    // // Obtener cliente logueado (opcional según tu sesión)
    // Cliente cliente = (Cliente)
    // request.getSession().getAttribute("clienteLogueado");

    // // Crear lista de cotizaciones a previsualizar
    // List<Cotizacion> cotizaciones = new ArrayList<>();

    // for (Map.Entry<Proveedor, List<Producto>> entry :
    // productosPorProveedor.entrySet()) {
    // Proveedor proveedor = entry.getKey();
    // List<Producto> productosDelProveedor = entry.getValue();

    // Cotizacion cotizacion = new Cotizacion();
    // cotizacion.setProveedor(proveedor);
    // cotizacion.setCliente(cliente);
    // cotizacion.setFechaCreacion(LocalDate.now());
    // cotizacion.setEstado(EstadoCotizacion.PENDIENTE);

    // List<CotizacionItem> items = new ArrayList<>();
    // double total = 0;

    // for (Producto producto : productosDelProveedor) {
    // CotizacionItem item = new CotizacionItem();
    // item.setProducto(producto);
    // item.setCantidad(1); // por defecto, 1 unidad
    // item.setPrecioUnitario(producto.getPrecio());
    // item.setCotizacion(cotizacion);

    // total += producto.getPrecio();
    // items.add(item);
    // }

    // cotizacion.setItems(items);
    // cotizacion.setMontoTotal(total);

    // cotizaciones.add(cotizacion);
    // }

    // datosModelado.addAttribute("cotizaciones", cotizaciones);

    // // Retorna la vista de previsualización
    // return new ModelAndView("previsualizar-cotizacion", datosModelado);
    // }

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

            // Obtener cliente logueado (opcional según tu sesión)
            Cliente cliente = (Cliente) request.getSession().getAttribute("clienteLogueado");

            // Crear lista de cotizaciones a previsualizar

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
                    item.setCantidad(1); // por defecto, 1 unidad
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
            //redirectAttributes.addFlashAttribute("cotizaciones", cotizaciones);

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
            List<Cotizacion> cotizaciones = (List<Cotizacion>) request.getSession()
                    .getAttribute("cotizaciones");
            if (cotizaciones == null || cotizaciones.isEmpty()) {
                // Redirigir si la sesión está vacía (ej. acceso directo o sesión expirada)
                return new ModelAndView("redirect:/catalogo");
            }

            // Limpiar la sesión inmediatamente después de leer los datos
            request.getSession().removeAttribute("cotizaciones");

            ModelMap datosModelado = new ModelMap();
            datosModelado.put("cotizaciones", cotizaciones);            
            
            // Obtener cliente logueado (opcional según tu sesión)
            // Cliente cliente = (Cliente)
            // request.getSession().getAttribute("clienteLogueado");

            // Crear lista de cotizaciones a previsualizar
            // datosModelado.addAttribute("cotizaciones", cotizaciones);
            // return "previsualizar-cotizacion";
            return new ModelAndView("previsualizar-cotizacion", datosModelado);

        } catch (Exception e) {
            e.printStackTrace();
            ModelMap datosModelado = new ModelMap();
            // return "redirect:/catalogo";
            return new ModelAndView("previsualizar-cotizacion", datosModelado);
        }

    }

}

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
import com.tallerwebi.dominio.entidades.CotizacionItem;
import com.tallerwebi.dominio.excepcion.NoHayProductoExistente;
import com.tallerwebi.dominio.servicios.ServicioClienteI;
import com.tallerwebi.dominio.servicios.ServicioCotizacion;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tallerwebi.presentacion.dto.UsuarioSesionDto;

@Controller
@RequestMapping("/cliente")
public class ControladorCliente   {

    private final ServicioClienteI servicioClienteI;
    private final ServicioCotizacion servicioCotizacion;  // inyección correcta

    // Constructor que Spring usa para inyección
    public ControladorCliente(ServicioClienteI servicioClienteI, ServicioCotizacion servicioCotizacion) {
        this.servicioClienteI = servicioClienteI;
        this.servicioCotizacion = servicioCotizacion;
    }


    @GetMapping("/dashboard")
    public ModelAndView irDashboard(HttpServletRequest request) {
        ModelMap datosModelado = new ModelMap();

        UsuarioSesionDto usuarioSesion = (UsuarioSesionDto) request.getSession().getAttribute("usuarioLogueado");
        String rol_cliente = "CLIENTE";

        if (usuarioSesion == null || !rol_cliente.equalsIgnoreCase(usuarioSesion.getRol()) || usuarioSesion.getUsername() == null) {
            return new ModelAndView("redirect:/login");
        }

    System.out.println("[ControladorCliente] usuarioSesion id=" + usuarioSesion.getId() + " rol=" + usuarioSesion.getRol());
    datosModelado.put("nombreCliente", usuarioSesion.getNombre());
        datosModelado.put("apellidoCliente", usuarioSesion.getApellido());
        datosModelado.put("rolCliente", usuarioSesion.getRol());
    
        try {
            List<Cotizacion> cotizaciones = servicioCotizacion.obtenerCotizacionPorIdCliente(usuarioSesion.getId());
            if (cotizaciones == null) {
                cotizaciones = new ArrayList<>();
            }
            datosModelado.put("cotizaciones", cotizaciones);
            datosModelado.put("mensaje", cotizaciones.isEmpty() ? "No hay cotizaciones" : "Hay cotizaciones disponibles");
            // Serializar una versión simplificada a JSON para JS en la vista
            try {
                List<Map<String, Object>> simplified = new ArrayList<>();
                for (Cotizacion c : cotizaciones) {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", c.getId());
                    m.put("monto", c.getMontoTotal());
                    m.put("estado", c.getEstado() == null ? null : c.getEstado().toString());
                    m.put("proveedor", c.getProveedor() == null ? null : c.getProveedor().getRazonSocial());
                    m.put("fecha", c.getFechaCreacion() == null ? null : c.getFechaCreacion().toString());
                    List<Map<String, Object>> items = new ArrayList<>();
                    List<CotizacionItem> itemsList = c.getItems() == null ? new ArrayList<>() : c.getItems();
                    for (CotizacionItem it : itemsList) {
                        Map<String, Object> im = new HashMap<>();
                        im.put("producto", it.getProducto() == null ? null : it.getProducto().getNombre());
                        im.put("cantidad", it.getCantidad());
                        im.put("precio", it.getPrecioUnitario());
                        items.add(im);
                    }
                    m.put("items", items);
                    simplified.add(m);
                }
                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(simplified);
                datosModelado.put("cotizacionesJson", json);
            } catch (Exception ex) {
                ex.printStackTrace();
                datosModelado.put("cotizacionesJson", "[]");
            }
        } catch (NoHayProductoExistente e) {
            datosModelado.put("cotizaciones", new ArrayList<>());
            datosModelado.put("error", "No hay cotizaciones disponibles");
        }
        return new ModelAndView("dashboard", datosModelado);
    }
}

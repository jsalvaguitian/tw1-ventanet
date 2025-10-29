package com.tallerwebi.presentacion;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import com.tallerwebi.dominio.entidades.Cotizacion;
import com.tallerwebi.dominio.excepcion.NoHayProductoExistente;
import com.tallerwebi.dominio.servicios.ServicioClienteI;
import com.tallerwebi.dominio.servicios.ServicioCotizacion;
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

        if (usuarioSesion == null || !rol_cliente.equalsIgnoreCase(usuarioSesion.getRol())) {
            return new ModelAndView("redirect:/login");
        }

        datosModelado.put("nombreCliente", usuarioSesion.getNombre());
        datosModelado.put("apellidoCliente", usuarioSesion.getApellido());
        datosModelado.put("rolCliente", usuarioSesion.getRol());
    
        try {
            List<Cotizacion> cotizaciones = servicioCotizacion.obtenerCotizacionPorIdCliente(usuarioSesion.getId());
            datosModelado.put("cotizaciones", cotizaciones);
            datosModelado.put("mensaje", cotizaciones.isEmpty() ? "No hay cotizaciones" : "Hay cotizaciones disponibles");
        } catch (NoHayProductoExistente e) {
            datosModelado.put("cotizaciones", new ArrayList<>());
            datosModelado.put("error", "No hay cotizaciones disponibles");
        }
        return new ModelAndView("dashboard", datosModelado);
    }
}

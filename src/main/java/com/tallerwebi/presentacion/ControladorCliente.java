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
import com.tallerwebi.dominio.entidades.Presupuesto;
import com.tallerwebi.dominio.excepcion.NoHayProductoExistente;
import com.tallerwebi.dominio.servicios.ServicioClienteI;
import com.tallerwebi.dominio.servicios.ServicioPresupuesto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tallerwebi.presentacion.dto.UsuarioSesionDto;

@Controller
@RequestMapping("/cliente")
public class ControladorCliente {

    private final ServicioClienteI servicioClienteI;
    private final ServicioPresupuesto servicioPresupuesto; // inyección correcta

    // Constructor que Spring usa para inyección
    public ControladorCliente(ServicioClienteI servicioClienteI, ServicioPresupuesto servicioPresupuesto) {
        this.servicioClienteI = servicioClienteI;
        this.servicioPresupuesto = servicioPresupuesto;
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
            List<Presupuesto> presupuestos = servicioPresupuesto.obtenerPresupuestosPorIdUsuario(usuarioSesion.getId());
            if (presupuestos == null) {
                presupuestos = new ArrayList<>();
            }
            datosModelado.put("presupuestos", presupuestos);
            datosModelado.put("mensaje",
                    presupuestos.isEmpty() ? "No hay presupuestos" : "Hay presupuestos disponibles");
            // Serializar una versión simplificada a JSON para JS en la vista
            try {
                List<Map<String, Object>> simplified = new ArrayList<>();
                for (Presupuesto p : presupuestos) {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", p.getId());
                    m.put("fecha", p.getFechaCreacion() == null ? null : p.getFechaCreacion().toString());
                    m.put("provincia", p.getProvincia() == null ? null : p.getProvincia().getNombre());
                    m.put("localidad", p.getLocalidad() == null ? null : p.getLocalidad().getNombre());
                    m.put("partido", p.getPartido() == null ? null : p.getPartido().getNombre());
                    simplified.add(m);
                }

                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(simplified);
                datosModelado.put("presupuestosJson", json);
            } catch (Exception ex) {
                ex.printStackTrace();
                datosModelado.put("presupuestosJson", "[]");
            }
        } catch (NoHayProductoExistente e) {
            datosModelado.put("presupuestos", new ArrayList<>());
            datosModelado.put("error", "No hay presupuestos disponibles");
        }

        return new ModelAndView("dashboard", datosModelado);
    }
}

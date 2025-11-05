package com.tallerwebi.presentacion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.tallerwebi.dominio.entidades.Localidad;
import com.tallerwebi.dominio.entidades.Partido;
import com.tallerwebi.dominio.entidades.TipoVentana;
import com.tallerwebi.dominio.servicios.ServicioTablas;
import com.tallerwebi.dominio.servicios.ServicioTipoProducto;
import com.tallerwebi.dominio.servicios.ServicioTipoVentana;

@Controller
public class ControladorPresupuesto {

    
    private final ServicioTipoProducto servicioTipoProducto;
    private final ServicioTipoVentana servicioTipoVentana;
    private final ServicioTablas servicioTablas;

    public ControladorPresupuesto(ServicioTipoProducto servicioTipoProducto, ServicioTipoVentana servicioTipoVentana,
            ServicioTablas servicioTablas) {
        this.servicioTipoProducto = servicioTipoProducto;
        this.servicioTipoVentana = servicioTipoVentana;
        this.servicioTablas = servicioTablas;
    }

    @GetMapping("/presupuesto")
    public String mostrarFormulario(Model model) {
        // agregar tipos de producto al modelo para llenar el select
        model.addAttribute("tiposProducto", servicioTipoProducto.obtener());
        model.addAttribute("colores", servicioTablas.obtenerColores());
        model.addAttribute("materiales", servicioTablas.obtenerMateriales());
        model.addAttribute("altos", servicioTablas.obtenerAltos());
        model.addAttribute("anchos", servicioTablas.obtenerAnchos());
        model.addAttribute("provincias", servicioTablas.obtenerProvincias());

        // tipoVentana will be loaded dynamically based on the selected tipoProducto via
        // AJAX
        return "presupuesto";
    }

    /**
     * Endpoint utilizado por la vista para obtener las categorias (TipoVentana) de
     * un producto.
     * Retorna un JSON con lista de objetos {id,nombre}.
     */

    @GetMapping("/presupuesto/tipo-ventana")
    @ResponseBody
    public List<Map<String, Object>> obtenerTipoVentanaPorTipoDeProducto(
            @RequestParam("tipoProductoId") Long tipoProductoId) {
        List<TipoVentana> lista = servicioTipoVentana.obtenerPorIdTipoProducto(tipoProductoId);
        return lista.stream().map(tv -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", tv.getId());
            m.put("nombre", tv.getNombre());
            return m;
        }).collect(Collectors.toList());
    }

    @GetMapping("/presupuesto/provincia")
    @ResponseBody
    public List<Map<String, Object>> obtenerLocalidadesPorProvincia(@RequestParam("provinciaId") Long provinciaId) {
        List<Localidad> lista = servicioTablas.obtenerLocalidadesPorProvincia(provinciaId);
        return lista.stream().map(l -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id_localidad", l.getId_localidad());
            m.put("nombre", l.getNombre());
            return m;
        }).collect(Collectors.toList());
    }

    @GetMapping("/presupuesto/localidad")
    @ResponseBody
    public List<Map<String, Object>> obtenerPartidosPorLocalidad(@RequestParam("localidadId") Long localidadId) {
        List<Partido> lista = servicioTablas.obtenerPartidosPorLocalidad(localidadId);
        return lista.stream().map(p -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id_partido", p.getId_partido());
            m.put("nombre", p.getNombre());
            return m;
        }).collect(Collectors.toList());
    }

    @PostMapping("/presupuesto")
    public String procesarFormulario(
            @RequestParam String tipoVentana,
            @RequestParam Double ancho,
            @RequestParam Double alto,
            @RequestParam String material,
            @RequestParam String vidrio,
            @RequestParam String color,
            @RequestParam(required = false) boolean premarco,
            @RequestParam(required = false) boolean barrotillos,
            Model model) {

        model.addAttribute("mensaje", "Tu solicitud fue enviada!");
        return "home";
    }
}

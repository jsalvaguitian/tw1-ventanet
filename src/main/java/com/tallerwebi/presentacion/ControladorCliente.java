package com.tallerwebi.presentacion;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.tallerwebi.dominio.servicios.ServicioClienteI;
import com.tallerwebi.presentacion.dto.UsuarioSesionDto;
@Controller
@RequestMapping("/cliente")
public class ControladorCliente {

     private final  ServicioClienteI servicioClienteI;

    public ControladorCliente(ServicioClienteI servicioClienteI) {
        this.servicioClienteI = servicioClienteI;
    }

    @GetMapping("/dashboard")
    public ModelAndView irDashboard(HttpServletRequest request){
        ModelMap datosModelado = new ModelMap();

        UsuarioSesionDto usuarioSesion = (UsuarioSesionDto) request.getSession().getAttribute("usuarioLogueado");
        String rol_cliente = "CLIENTE";

        if(usuarioSesion == null || !rol_cliente.equalsIgnoreCase(usuarioSesion.getRol())){
            return new ModelAndView("redirect:/login");
        }

        datosModelado.put("mailCliente", usuarioSesion.getUsername());
        return new ModelAndView("dashboard", datosModelado);
   }


}

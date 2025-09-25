package com.tallerwebi.presentacion;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.tallerwebi.dominio.servicios.ServicioProveedorI;
import com.tallerwebi.presentacion.dto.UsuarioSesionDto;

@Controller
@RequestMapping("/proveedor")
public class ControladorProveedor {

    private ServicioProveedorI servicioProveedorI;


    public ControladorProveedor(ServicioProveedorI servicioProveedorI) {
        this.servicioProveedorI = servicioProveedorI;
    }

    @GetMapping("/dashboard")
    public ModelAndView irDashboard(HttpServletRequest request){
        ModelMap datosModelado = new ModelMap();

        UsuarioSesionDto usuarioSesion = (UsuarioSesionDto) request.getSession().getAttribute("usuarioLogueado");
        String rol_proveedor = "PROVEEDOR";

        if(usuarioSesion == null || !rol_proveedor.equalsIgnoreCase(usuarioSesion.getRol())){
            return new ModelAndView("redirect:/login-user");
        }

        datosModelado.put("mailProveedor", usuarioSesion.getUsername());
        return new ModelAndView("dashboard-proveedor", datosModelado);
   }



}

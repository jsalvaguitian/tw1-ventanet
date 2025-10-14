package com.tallerwebi.presentacion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.tallerwebi.dominio.entidades.ResetearPasswordToken;
import com.tallerwebi.dominio.servicios.ServicioCambiarPassword;
import com.tallerwebi.dominio.servicios.ServicioRecuperarPassword;

@Controller
public class ControladorCambiarPassword {
    private ServicioCambiarPassword servicioCambiarPassword;
    private ServicioRecuperarPassword servicioRecuperarPassword;

    @Autowired
    public ControladorCambiarPassword(ServicioCambiarPassword servicioCambiarPassword,
            ServicioRecuperarPassword servicioRecuperarPassword) {
        this.servicioCambiarPassword = servicioCambiarPassword;
        this.servicioRecuperarPassword = servicioRecuperarPassword;
    }

    @GetMapping("/cambiar-password")
    public ModelAndView mostrarFormilarioCambiarPassword(@RequestParam("token") String token) {
        ModelAndView mav = new ModelAndView("cambiar-password");
        ResetearPasswordToken resetToken = servicioRecuperarPassword.buscarPorToken(token);
        if (resetToken == null || resetToken.isUsado()) {
            mav.setViewName("token-invalido");
            return mav;
        }

        mav.addObject("token", token);
        return mav;
    }

    @PostMapping("/cambiar-password")
    public ModelAndView cambiarPassword(@RequestParam(name = "password") String nuevaPassword,
            @RequestParam("token") String token) {
        ModelMap modelMap = new ModelMap();
        System.out.println("DEBUGG >> token en el controlador = " + token);
        if (nuevaPassword.isEmpty() || nuevaPassword.isBlank()) {
            modelMap.put("password_vacio", "Debe ingresar una contraseña");
        } else {
            try {
                boolean operacion_exitosa = servicioCambiarPassword.cambiarPassword(nuevaPassword, token);
                if (operacion_exitosa) {
                    modelMap.put("exito", "La contraña se ha reestablecido");
                } else {
                    modelMap.put("error", "A ocurrido un error");
                }
            } catch (Exception e) {
            }
        }

        return new ModelAndView("recuperar-password", modelMap);
    }
}

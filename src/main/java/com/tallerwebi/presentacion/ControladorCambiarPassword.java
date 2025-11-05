package com.tallerwebi.presentacion;

import javax.servlet.http.HttpServletRequest;

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
import com.tallerwebi.presentacion.dto.UsuarioSesionDto;

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
            @RequestParam("token") String token, @RequestParam(name = "repetirPassword") String repetirPassword) {
        ModelMap modelMap = new ModelMap();

        if (nuevaPassword.isEmpty() || nuevaPassword.isBlank()) {
            modelMap.put("password_vacio", "Debe ingresar una contraseña");
            modelMap.put("token", token);
            return new ModelAndView("cambiar-password", modelMap);
        }
        if (repetirPassword.isEmpty() || repetirPassword.isBlank()) {
            modelMap.put("password_vacio", "Debe repetir la contraseña");
            modelMap.put("token", token);
            return new ModelAndView("cambiar-password", modelMap);
        }
        if (!repetirPassword.equals(nuevaPassword)) {
            modelMap.put("password_no_coincide", "Las contraseñas no coinciden");
            modelMap.put("token", token);
            return new ModelAndView("cambiar-password", modelMap);
        }
        // Validar Contrasenia
        if (nuevaPassword.length() < 8 ||
                !nuevaPassword.matches(".*[A-Z].*") || // que tenga 1 mayuscula
                !nuevaPassword.matches(".*[a-z].*") || // que tenga 1 minuscula
                !nuevaPassword.matches(".*[^a-zA-Z0-9].*")) { // que tenga 1 simbolo
            modelMap.put("error",
                    "La contraseña debe tener al menos 8 caracteres, 1 mayúscula, 1 minúscula y 1 símbolo.");
            modelMap.put("token", token);
            return new ModelAndView("cambiar-password", modelMap);
        }
        try {
            boolean operacion_exitosa = servicioCambiarPassword.cambiarPassword(nuevaPassword, token);
            if (operacion_exitosa) {
                modelMap.put("exito", "La contraña se ha reestablecido");
            } else {
                modelMap.put("error", "A ocurrido un error");
            }
        } catch (Exception e) {
        }
        return new ModelAndView("redirect:/login", modelMap);
    }

    @GetMapping("/cambiar-password-perfil")
    public ModelAndView mostrarFormularioCambiarPasswordSinToken() {
        return new ModelAndView("cambiar-password-perfil");
    }

    @PostMapping("/cambiar-password-perfil")
    public ModelAndView cambiarPasswordSinToken(String nuevaPassword,
            @RequestParam(name = "repetirPassword") String repetirPassword, HttpServletRequest httpServletRequest) {
        ModelMap modelMap = new ModelMap();

        UsuarioSesionDto usuarioSesion = (UsuarioSesionDto) httpServletRequest.getSession()
                .getAttribute("usuarioLogueado");

        Long idUsuario = usuarioSesion.getId();

        if (nuevaPassword.isEmpty() || nuevaPassword.isBlank()) {
            modelMap.put("password_vacio", "Debe ingresar una contraseña");

            return new ModelAndView("cambiar-password-perfil", modelMap);
        }
        if (repetirPassword.isEmpty() || repetirPassword.isBlank()) {
            modelMap.put("password_vacio", "Debe repetir la contraseña");

            return new ModelAndView("cambiar-password-perfil", modelMap);
        }
        if (!repetirPassword.equals(nuevaPassword)) {
            modelMap.put("password_no_coincide", "Las contraseñas no coinciden");

            return new ModelAndView("cambiar-password-perfil", modelMap);
        }
        // Validar Contrasenia
        if (nuevaPassword.length() < 8 ||
                !nuevaPassword.matches(".*[A-Z].*") || // que tenga 1 mayuscula
                !nuevaPassword.matches(".*[a-z].*") || // que tenga 1 minuscula
                !nuevaPassword.matches(".*[^a-zA-Z0-9].*")) { // que tenga 1 simbolo
            modelMap.put("error",
                    "La contraseña debe tener al menos 8 caracteres, 1 mayúscula, 1 minúscula y 1 símbolo.");

            return new ModelAndView("cambiar-password-perfil", modelMap);
        }
        try {
            boolean operacion_exitosa = servicioCambiarPassword.cambiarPasswordUsuarioLogueado(nuevaPassword,
                    idUsuario);
            if (operacion_exitosa) {
                modelMap.put("exito", "La contraña se ha reestablecido");
            } else {
                modelMap.put("error", "A ocurrido un error");
            }
        } catch (Exception e) {
        }
        return new ModelAndView("redirect:/login", modelMap);
    }
}

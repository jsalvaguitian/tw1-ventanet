package com.tallerwebi.presentacion;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.excepcion.UsuarioInexistenteException;
import com.tallerwebi.dominio.servicios.ServicioRecuperarPassword;
import com.tallerwebi.dominio.servicios.ServicioUsuario;

@Controller
public class ControladorRecuperarPassword {
    private ServicioRecuperarPassword servicioRecuperarPassword;
    private ServicioUsuario servicioUsuario;

    @Autowired
    public ControladorRecuperarPassword(ServicioRecuperarPassword servicioRecuperarPassword,
            ServicioUsuario servicioUsuario) {
        this.servicioRecuperarPassword = servicioRecuperarPassword;
        this.servicioUsuario = servicioUsuario;

    }

    @GetMapping("recuperar-password")
    public ModelAndView mostrarFormRecuperarPassword() {
        return new ModelAndView("recuperar-password");
    }

    @PostMapping("recuperar-password")
    public ModelAndView recuperarPassword(HttpServletRequest httpServletRequest,
            @RequestParam(name = "email") String email) {
        ModelMap modelMap = new ModelMap();

        if (email.isEmpty() || email.isBlank()) {
            modelMap.put("email_vacio", "Debe ingresar un email");
            return new ModelAndView("recuperar-password", modelMap);
        }
        try {
            Usuario usuario = servicioUsuario.buscarPorMail(email);
            if (usuario == null) {
                modelMap.put("usuario_no_existe", "El email ingresado no esta registrado");
                return new ModelAndView("recuperar-password", modelMap);
            }

            if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
                modelMap.put("error", "El usuario no tiene un email valido");
                return new ModelAndView("recuperar-password", modelMap);
            }

            servicioRecuperarPassword.enviarEmailDeRecuperacion(usuario, httpServletRequest);
            modelMap.put("operacion_exitosa", "Se a enviado un codigo de recuperacion");
            return new ModelAndView("recuperar-password", modelMap);

        } catch (UsuarioInexistenteException exception) {
            modelMap.put("usuario_no_existe", "El email ingresado no esta registrado");
            return new ModelAndView("recuperar-password", modelMap);
        } catch (Exception e) {
            modelMap.put("error", "A ocurrido un error al procesar la solicitud");
            return new ModelAndView("recuperar-password", modelMap);
        }

    }
}

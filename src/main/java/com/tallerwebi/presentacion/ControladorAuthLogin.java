package com.tallerwebi.presentacion;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import com.tallerwebi.dominio.entidades.UsuarioAuth;
import com.tallerwebi.dominio.excepcion.UsuarioInexistente;
import com.tallerwebi.dominio.servicios.ServicioUsuarioI;
import com.tallerwebi.presentacion.dto.UsuarioDto;
import com.tallerwebi.presentacion.dto.UsuarioSesionDto;

import net.bytebuddy.asm.Advice.Return;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ControladorAuthLogin {

    private ServicioUsuarioI servicioUsuarioI;

    @Autowired
    public ControladorAuthLogin(ServicioUsuarioI servicioUsuarioI) {
        this.servicioUsuarioI = servicioUsuarioI;
    }

    @GetMapping("/login-user")
    public ModelAndView mostrarLogin() {
        ModelMap modeloLogin = new ModelMap();
        modeloLogin.put("usuarioDto", new UsuarioDto());
        return new ModelAndView("login-usuario", modeloLogin);
    }

    @PostMapping("/login-user")
    public ModelAndView procesarLogin(@ModelAttribute("usuarioDto") UsuarioDto usuarioDto, HttpServletRequest request) throws UsuarioInexistente {
        ModelMap datosAMostrar = new ModelMap();
        String emailIngresado = usuarioDto.getUsernameOrMail();
        String contraseniaIngresada = usuarioDto.getContrasenia();

        if (emailIngresado.isBlank())
            datosAMostrar.put("error_email_user", "Ingresá tu usuario o email.");
        if (contraseniaIngresada.isBlank())
            datosAMostrar.put("error_password", "Ingresá la contraseña");

        if (!emailTieneUnFormatoValido(emailIngresado))
            datosAMostrar.put("error_formato_email", "El formato del email es invalido");

        if (!emailIngresado.isBlank() && !contraseniaIngresada.isBlank() && emailTieneUnFormatoValido(emailIngresado)) {

            UsuarioAuth encontradoBd = this.servicioUsuarioI.autenticar(emailIngresado, contraseniaIngresada);
            HttpSession sesion = request.getSession();
            String rol = encontradoBd.getRol();

            // se crea un obj DTO para la sesion y asi otras vistas la consume
            UsuarioSesionDto usuarioSesion = new UsuarioSesionDto(encontradoBd.getId(), encontradoBd.getEmail(), rol);

            sesion.setAttribute("usuarioLogueado", usuarioSesion);
            if (rol.equalsIgnoreCase("ADMIN")) {
                return new ModelAndView("redirect:/admin/dashboard");
            } else {
                return new ModelAndView("redirect:/mapa");
            }

        } else {

            datosAMostrar.put("error_not_exist", "El usuario no se encuentra registrado.");
            return new ModelAndView("login-usuario", datosAMostrar);
        }

    }

    private boolean emailTieneUnFormatoValido(String emailIngresado) {
        return true;    
    }

}

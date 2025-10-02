package com.tallerwebi.presentacion;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import com.tallerwebi.dominio.entidades.UsuarioAuth;
import com.tallerwebi.dominio.excepcion.UsuarioInexistenteException;
import com.tallerwebi.dominio.servicios.ServicioUsuarioAuthI;
import com.tallerwebi.presentacion.dto.UsuarioDto;
import com.tallerwebi.presentacion.dto.UsuarioSesionDto;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ControladorAuthLogin {

    private ServicioUsuarioAuthI servicioUsuarioI;

    @Autowired
    public ControladorAuthLogin(ServicioUsuarioAuthI servicioUsuarioI) {
        this.servicioUsuarioI = servicioUsuarioI;
    }

    @GetMapping("/login-user")
    public ModelAndView mostrarLogin() {
        ModelMap modeloLogin = new ModelMap();
        modeloLogin.put("usuarioDto", new UsuarioDto());
        return new ModelAndView("login-usuario", modeloLogin);
    }

    @PostMapping("/login-user")
    public ModelAndView procesarLogin(@ModelAttribute("usuarioDto") UsuarioDto usuarioIngresado,
            HttpServletRequest request) {

        ModelMap datosMapeados = new ModelMap();
        String emailIngresado = usuarioIngresado.getEmail();
        String contraseniaIngresada = usuarioIngresado.getContrasenia();

        if (emailIngresado == null || emailIngresado.isBlank()) {
            datosMapeados.put("error_email", "Por favor, ingresa el email.");

        } else if (!emailTieneFormatoValido(emailIngresado)) {
            datosMapeados.put("error_email", "El formato del email es invalido");
        }

        if (contraseniaIngresada == null || contraseniaIngresada.isBlank()) {
            datosMapeados.put("error_password", "Por favor, ingresa la contraseña.");
        }

        if (!usuarioIngresado.getEmail().isBlank() && !usuarioIngresado.getContrasenia().isBlank()
                && emailTieneFormatoValido(emailIngresado)) {
            try {

                UsuarioAuth encontradoBd = servicioUsuarioI.autenticar(emailIngresado, contraseniaIngresada);
                String rolEncotrado = encontradoBd.getRol();

                HttpSession session = request.getSession();
                UsuarioSesionDto usuarioSesion = new UsuarioSesionDto(encontradoBd.getId(), encontradoBd.getEmail(), encontradoBd.getRol());
                session.setAttribute("usuarioLogueado", usuarioSesion);

                //se podria hacer un dashboard dinamico y tener fragmentos y asi no hacer un switch o ifes
                if (rolEncotrado.equalsIgnoreCase("PROVEEDOR")) {
                    return new ModelAndView("redirect:/proveedor/dashboard-proveedor");
                
                } else if (rolEncotrado.equalsIgnoreCase("CLIENTE")) {
                    return new ModelAndView("redirect:/cliente/dashboard"); 

                } /*else if (rolEncotrado.equalsIgnoreCase("FABRICANTE")) {
                    return new ModelAndView("redirect:/admin/dashboard");

                } else if (rolEncotrado.equalsIgnoreCase("ADMIN")) {
                    return new ModelAndView("redirect:/admin/dashboard");
                }*/

            } catch (UsuarioInexistenteException e) {
                datosMapeados.put("error_coincidencia", "El usuario no se encuentra registrado.");
                datosMapeados.put("usuarioDto", usuarioIngresado);
                return new ModelAndView("login-usuario", datosMapeados);
            }

        } 
        datosMapeados.put("usuarioDto", usuarioIngresado);
        return new ModelAndView("login-usuario", datosMapeados);
        
    }

    @GetMapping("/logout")
    public String logOut(HttpServletRequest request) {
        request.getSession().invalidate();
        return "redirect:/login-user";
    }

    private boolean emailTieneFormatoValido(String emailIngresado) {
        return emailIngresado.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }


}

package com.tallerwebi.presentacion;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import com.tallerwebi.dominio.entidades.UsuarioAuth;
import com.tallerwebi.dominio.excepcion.UsuarioInexistenteException;
import com.tallerwebi.dominio.servicios.ServicioUsuarioI;
import com.tallerwebi.presentacion.dto.UsuarioDto;
import com.tallerwebi.presentacion.dto.UsuarioSesionDto;

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
    public ModelAndView procesarLogin(@ModelAttribute("usuarioDto") UsuarioDto usuarioDto, HttpServletRequest request) {

        ModelMap datosAMostrar = new ModelMap();
        String emailIngresado = usuarioDto.getEmail();
        String contraseniaIngresada = usuarioDto.getContrasenia();

        // Validaciones básicas antes de llamar al servicio
        if (emailIngresado.isBlank()) datosAMostrar.put("error_email", "Ingresá tu email.");
        if (contraseniaIngresada.isBlank()) datosAMostrar.put("error_password", "Ingresá la contraseña");
        if (!emailIngresado.isBlank() && !emailTieneUnFormatoValido(emailIngresado))
            datosAMostrar.put("error_email", "El formato del email es inválido");

        // Si hay errores, retornamos la vista de login
        if (!datosAMostrar.isEmpty()) {
            datosAMostrar.put("usuarioDto", usuarioDto);
            return new ModelAndView("login-usuario", datosAMostrar);
        }

        // Intentamos autenticar
        try {
            UsuarioAuth encontradoBd = servicioUsuarioI.autenticar(emailIngresado, contraseniaIngresada);

            // Por seguridad, chequeamos null aunque el servicio lance excepción
            if (encontradoBd == null) {
                datosAMostrar.put("error_coincidencia", "Usuario o contraseña incorrectos.");
                datosAMostrar.put("usuarioDto", usuarioDto);
                return new ModelAndView("login-usuario", datosAMostrar);
            }

            // Crear objeto de sesión
            HttpSession sesion = request.getSession();
            UsuarioSesionDto usuarioSesion = new UsuarioSesionDto(
                    encontradoBd.getId(),
                    encontradoBd.getEmail(),
                    encontradoBd.getRol()
            );
            sesion.setAttribute("usuarioLogueado", usuarioSesion);

            // Redireccionar según rol
            if ("Proveedor".equalsIgnoreCase(encontradoBd.getRol())) {
                return new ModelAndView("redirect:/proveedor/dashboard");
            } else {
                return new ModelAndView("redirect:/cliente/dashboard");
            }

        } catch (UsuarioInexistenteException e) {
            datosAMostrar.put("error_coincidencia", "El usuario no se encuentra registrado.");
            datosAMostrar.put("usuarioDto", usuarioDto);
            return new ModelAndView("login-usuario", datosAMostrar);
        }
    }

    // Método auxiliar para validar formato de email
    private boolean emailTieneUnFormatoValido(String email) {
        return email != null && email.matches("^[\\w-.]+@[\\w-]+\\.[a-z]{2,}$");
    }

        
      

}

package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.excepcion.ContraseniaInvalida;
import com.tallerwebi.dominio.excepcion.EmailInvalido;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import com.tallerwebi.dominio.servicios.ServicioLogin;
import com.tallerwebi.presentacion.dto.DatosLogin;
import com.tallerwebi.presentacion.dto.UsuarioDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ControladorAutenticacion {

    private ServicioLogin servicioLogin;

    @Autowired
    public ControladorAutenticacion(ServicioLogin servicioLogin) {
        this.servicioLogin = servicioLogin;
    }

    @RequestMapping("/login")
    public ModelAndView irALogin() {

        ModelMap modelo = new ModelMap();
        modelo.put("datosLogin", new DatosLogin());
        return new ModelAndView("login", modelo);
    }

    @RequestMapping(path = "/validar-login", method = RequestMethod.POST)
    public ModelAndView validarLogin(@ModelAttribute("datosLogin") DatosLogin datosLogin, HttpServletRequest request) {
        ModelMap model = new ModelMap();

        Usuario usuarioBuscado = servicioLogin.consultarUsuario(datosLogin.getEmail(), datosLogin.getPassword());
        if (usuarioBuscado != null) {
            request.getSession().setAttribute("ROL", usuarioBuscado.getRol());
            if (usuarioBuscado.getRol().equals("CLIENTE")) {
                return new ModelAndView("redirect:/dashboard");
            }else if (usuarioBuscado.getRol().equals("PROVEEDOR")) {
                return new ModelAndView("redirect:/proveedor/dashboard-proveedor");
            } else if (usuarioBuscado.getRol().equals("ADMIN")) {
                return new ModelAndView("redirect:/admin/dashboard-admin");
            }
            
        } else {
            model.put("error", "Usuario o clave incorrecta");
        }
        return new ModelAndView("login", model);
    }

    @RequestMapping(path = "/registrarme", method = RequestMethod.POST)
    public ModelAndView registrarme(@ModelAttribute("usuario") Usuario usuario, HttpServletRequest request) {
        // request confirmar password
        String confirmarPassword = request.getParameter("confirmarPassword");
        // Comparar contrasenias
        if (!usuario.getPassword().equals(confirmarPassword)) {
            ModelMap model = new ModelMap();
            model.put("error", "Las contraseñas no coinciden");
            return new ModelAndView("nuevo-usuario", model);
        }
        try {
            servicioLogin.registrar(usuario);
        } catch (UsuarioExistente e) {
            ModelMap model = new ModelMap();
            model.put("error", "El usuario ya existe");
            return new ModelAndView("nuevo-usuario", model);
        } catch (ContraseniaInvalida e) {
            ModelMap model = new ModelMap();
            model.put("error", "La contraseña debe tener al menos 8 caracteres, 1 mayúscula, 1 minúscula y 1 símbolo.");
            return new ModelAndView("nuevo-usuario", model);
        } catch (EmailInvalido e) {
            ModelMap model = new ModelMap();
            model.put("error", "Email invalido");
            return new ModelAndView("nuevo-usuario", model);
        } catch (Exception e) {
            ModelMap model = new ModelMap();
            model.put("error", "Error al registrar el nuevo usuario");
            return new ModelAndView("nuevo-usuario", model);
        }
        return new ModelAndView("redirect:/login");
    }

    
    @RequestMapping(path = "/nuevo-usuario", method = RequestMethod.GET)
    public ModelAndView nuevoUsuario() {
        ModelMap model = new ModelMap();
        model.put("usuario", new Usuario());
        return new ModelAndView("nuevo-usuario", model);
    }

    @RequestMapping(path = "/home", method = RequestMethod.GET)
    public ModelAndView irAHome() {
        return new ModelAndView("home");
    }

    @RequestMapping(path = "/dashboard", method = RequestMethod.GET)
    public ModelAndView irADashboard() {
        return new ModelAndView("dashboard");
    }

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public ModelAndView inicio() {
        return new ModelAndView("redirect:/home");
    }

    @RequestMapping(path = "/registro-proveedor", method = RequestMethod.GET)
    public ModelAndView irRegistroProveedor() {
        ModelMap model = new ModelMap();
        model.put("usuarioProveedorDto", new UsuarioDto());
        return new ModelAndView("nuevo-proveedor", model);
    }
}

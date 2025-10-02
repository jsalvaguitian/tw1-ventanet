package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.entidades.Proveedor;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.excepcion.ContraseniaInvalida;
import com.tallerwebi.dominio.excepcion.CuitInvalido;
import com.tallerwebi.dominio.excepcion.EmailInvalido;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import com.tallerwebi.dominio.servicios.ServicioUsuario;
import com.tallerwebi.presentacion.dto.DatosLogin;
import com.tallerwebi.presentacion.dto.UsuarioDto;
import com.tallerwebi.presentacion.dto.UsuarioProveedorDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
public class ControladorAutenticacion {

    private ServicioUsuario servicioUsuario;

    @Autowired
    public ControladorAutenticacion(ServicioUsuario servicioLogin) {
        this.servicioUsuario = servicioLogin;
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

        Usuario usuarioBuscado = servicioUsuario.consultarUsuario(datosLogin.getEmail(), datosLogin.getPassword());
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
            servicioUsuario.registrar(usuario);
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
        model.put("usuarioProveedorDto", new UsuarioProveedorDTO());
        return new ModelAndView("nuevo-proveedor", model);
    }

    /*
     * Procesar el formulario de registro de proveedor
     * Contrasenia invalida
     * Cuit invalido
     * Email invalido
     * Usuario existente
     * Error general
     */

    @PostMapping("/registro-proveedor")
    public ModelAndView procesarRegistroProveedor(@ModelAttribute UsuarioProveedorDTO usuarioProveedorDTO) {
        ModelMap datos = new ModelMap();
        Usuario usuarioProveedor = usuarioProveedorDTO.obtenerEntidad();


        if (usuarioProveedor.getEmail() == null || usuarioProveedor.getEmail().isBlank()) {
            datos.put("error_mail", "Por favor, ingresa el email.");

        }else if(!emailTieneFormatoValido(usuarioProveedor.getEmail())) {
            datos.put("error_mail", "El formato del email es invalido");
        }
        
        if (usuarioProveedor.getPassword() == null || usuarioProveedor.getPassword().isBlank()) {
            datos.put("error_pswd","la contraseña es obligatoria.");
        }
        
        if (usuarioProveedor.getRazonSocial() == null || usuarioProveedor.getRazonSocial().isBlank()) {
            datos.put("error_razon_social","La razón social es obligatoria.");
        }
        
        String cuit = usuarioProveedor.getCuit();
        if (cuit == null || cuit.toString().isBlank() || !cuit.matches("\\d{11}")) {
            datos.put("error_cuit", "El CUIT es obligatorio y debe tener 11 dígitos.");
        }

        if(usuarioProveedorDTO.getDocumento() == null || usuarioProveedorDTO.getDocumento().isEmpty()) {
            datos.put("error_documento", "El documento es obligatorio.");
        }

        if(!usuarioProveedor.getEmail().isBlank() && !usuarioProveedor.getPassword().isBlank() && emailTieneFormatoValido(usuarioProveedor.getEmail()) && !usuarioProveedorDTO.getDocumento().isEmpty()){
           
           
            try {
               servicioUsuario.registrarProveedor(usuarioProveedor, usuarioProveedorDTO.getDocumento());
               return new ModelAndView("redirect:/info-registro-proveedor");

            } catch (ContraseniaInvalida e) {
                datos.put("error_pswd", e.getMessage());
            } catch (UsuarioExistente e) {
                datos.put("error_mail", "El usuario ya existe.");
            
            } catch (IOException e) {
                datos.put("error_documento", e.getMessage());

            } catch (CuitInvalido e) {
                datos.put("error_cuit", "El CUIT es invalido.");
            }
            
            catch (Exception e) {
                datos.put("error_general", "Error al registrar el nuevo proveedor.");
            }
        }
        
        datos.put("usuarioProveedorDto", usuarioProveedorDTO);
        return new ModelAndView("registro_proveedor", datos);
    }

    private boolean emailTieneFormatoValido(String email) {
                return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }
    


}

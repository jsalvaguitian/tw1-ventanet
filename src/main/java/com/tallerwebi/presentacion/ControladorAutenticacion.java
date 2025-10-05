package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.entidades.Cliente;
import com.tallerwebi.dominio.entidades.Proveedor;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.excepcion.ContraseniaInvalida;
import com.tallerwebi.dominio.excepcion.CuitInvalido;
import com.tallerwebi.dominio.excepcion.EmailInvalido;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import com.tallerwebi.dominio.excepcion.UsuarioInexistenteException;
import com.tallerwebi.dominio.servicios.ServicioUsuario;
import com.tallerwebi.presentacion.dto.DatosLogin;

import com.tallerwebi.presentacion.dto.UsuarioProvDTO;
import com.tallerwebi.presentacion.dto.UsuarioSesionDto;

import net.bytebuddy.asm.Advice.Return;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ControladorAutenticacion {

    private ServicioUsuario servicioUsuario;

    @Autowired
    public ControladorAutenticacion(ServicioUsuario servicioUsuario) {
        this.servicioUsuario = servicioUsuario;
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
        String emailIngresado = datosLogin.getEmail();
        String contraseniaIngresada = datosLogin.getPassword();

        if (emailIngresado == null || emailIngresado.isBlank()) {
            model.put("error_email", "Por favor, ingresa el email.");

        } else if (!emailTieneFormatoValido(emailIngresado)) {
            model.put("error_email", "El formato del email es invalido");
        }

        if (contraseniaIngresada == null || contraseniaIngresada.isBlank()) {
            model.put("error_password", "Por favor, ingresa la contraseña.");
        }

        if (!emailIngresado.isBlank() && !contraseniaIngresada.isBlank() && emailTieneFormatoValido(emailIngresado)) {

            try {
                Usuario usuarioBuscado = servicioUsuario.consultarUsuario(datosLogin.getEmail(),
                        datosLogin.getPassword());
                String rol = usuarioBuscado.getRol();
                UsuarioSesionDto usuarioSesion = new UsuarioSesionDto(usuarioBuscado.getId(), usuarioBuscado.getEmail(),
                        rol);
                request.getSession().setAttribute("usuarioLogueado", usuarioSesion);

                if (rol.equalsIgnoreCase("CLIENTE")) {
                    return new ModelAndView("redirect:/dashboard");

                } else if (rol.equalsIgnoreCase("PROVEEDOR")) {
                    return new ModelAndView("redirect:/proveedor/dashboard-proveedor");

                } else if (rol.equalsIgnoreCase("ADMIN")) {
                    return new ModelAndView("redirect:/admin/dashboard-admin");
                }

            } catch (UsuarioInexistenteException e) {
                model.put("error", "Usuario o clave incorrecta");
                model.put("datosLogin", datosLogin);
                return new ModelAndView("login", model);
            }

        }
        model.put("datosLogin", datosLogin);
        return new ModelAndView("login", model);
    }

    @RequestMapping(path = "/registrarme", method = RequestMethod.POST)
    public ModelAndView registrarme(@ModelAttribute("usuario") Cliente usuario, HttpServletRequest request) {
        // request confirmar password
        String confirmarPassword = request.getParameter("confirmarPassword");
        // Comparar contrasenias
        if (!usuario.getPassword().equals(confirmarPassword)) {
            ModelMap model = new ModelMap();
            model.put("error", "Las contraseñas no coinciden");
            return new ModelAndView("nuevo-usuario", model);
        }
        try {
            // guardar a cliente
            Cliente cliente = new Cliente();
            cliente.setEmail(usuario.getEmail());
            cliente.setPassword(usuario.getPassword());
            cliente.setNombre(usuario.getNombre());

            servicioUsuario.registrar(cliente);
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
            model.put("usuario", usuario);

            return new ModelAndView("nuevo-usuario", model);
        }
        return new ModelAndView("redirect:/login");
    }

    @RequestMapping(path = "/nuevo-usuario", method = RequestMethod.GET)
    public ModelAndView nuevoUsuario() {
        ModelMap model = new ModelMap();
        model.put("usuario", new Cliente());
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

    @RequestMapping(path = "/info-registro-proveedor", method = RequestMethod.GET)
    public ModelAndView irAInformacionRegistroProveedor() {
        return new ModelAndView("info-registro-proveedor");
    }

    @RequestMapping(path = "/registro-proveedor", method = RequestMethod.GET)
    public ModelAndView irARegistroProveedor() {
        ModelMap modelo = new ModelMap();
        modelo.put("usuarioProveedorDTO", new UsuarioProvDTO());
        return new ModelAndView("nuevo-proveedor", modelo);
    }

    @PostMapping("/registro-proveedor")
    public ModelAndView procesarRegistroProveedor(@ModelAttribute UsuarioProvDTO usuarioProvDto) {
        ModelMap datos = new ModelMap();
        Proveedor usuarioProv = usuarioProvDto.obtenerEntidad();

        if (estaVacioEmail(usuarioProv.getEmail())) {
            datos.put("error_email", "El email es obligatorio");

        } else if (!emailTieneFormatoValido(usuarioProv.getEmail())) {
            datos.put("error_email", "El email no tiene un formato válido");
        }

        if (usuarioProv.getPassword() == null || usuarioProv.getPassword().isBlank()) {
            datos.put("error_password", "La contraseña es obligatoria");
        }

        if (usuarioProv.getRazonSocial() == null || usuarioProv.getRazonSocial().isBlank()) {
            datos.put("error_razon_social", "La razón social es obligatoria");
        }

        if (usuarioProv.getRubro() == null) {
            datos.put("error_rubro", "El rubro es obligatorio");
        }

        String cuit = usuarioProv.getCuit();
        if (!esUnNumeroCuit(cuit)) {
            datos.put("error_cuit", "El CUIT es obligatorio y debe tener 11 dígitos numéricos");
        }

        if (usuarioProvDto.getDocumento() == null || usuarioProvDto.getDocumento().isEmpty()) {
            datos.put("error_documento", "El documento es obligatorio.");
        }

        if (esUnNumeroCuit(cuit) && emailTieneFormatoValido(usuarioProv.getEmail())
                && !estaVacioPassword(usuarioProv.getPassword())
                && !estaVacioRazonSocial(usuarioProv.getRazonSocial()) && usuarioProv.getRubro() != null
                && usuarioProvDto.getDocumento() != null && !usuarioProvDto.getDocumento().isEmpty()) {

            try {
                servicioUsuario.registrarProveedor(usuarioProv, usuarioProvDto.getDocumento());
                return new ModelAndView("redirect:/info-registro-proveedor");

            } catch (ContraseniaInvalida e) {
                datos.put("error_password",
                        "El formato de la contraseña es inválido. Debe tener al menos 8 caracteres, 1 mayúscula, 1 minúscula y 1 símbolo.");

            } catch (CuitInvalido e) {

                datos.put("error_cuit", "El CUIT es inválido");
            } catch (UsuarioExistente e) {

                datos.put("error_ya_existe", "El email o CUIT ya se encuentran registrados");
            } catch (IOException e) {
                datos.put("error_documento", "Error al guardar el documento. Intente nuevamente.");
            }
        }

        datos.put("usuarioProveedorDTO", usuarioProvDto);
        return new ModelAndView("nuevo-proveedor", datos);
    }

    private boolean estaVacioRazonSocial(String razonSocial) {
        return razonSocial == null || razonSocial.isBlank();
    }

    private boolean estaVacioPassword(String password) {
        return password == null || password.isBlank();
    }

    private boolean estaVacioEmail(String email) {
        return email == null || email.isBlank();
    }

    private boolean emailTieneFormatoValido(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    }

    private boolean esUnNumeroCuit(String cuit) {
        return cuit != null && !cuit.isBlank() && cuit.length() == 11 && cuit.matches("\\d+");
    }

    
}

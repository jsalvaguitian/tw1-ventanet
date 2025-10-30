package com.tallerwebi.presentacion;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.tallerwebi.dominio.entidades.Proveedor;
import com.tallerwebi.dominio.enums.EstadoUsuario;
import com.tallerwebi.dominio.servicios.ServicioAdministrador;
import com.tallerwebi.dominio.servicios.ServicioClienteI;
import com.tallerwebi.dominio.servicios.ServicioEmail;
import com.tallerwebi.dominio.servicios.ServicioProveedorI;
import com.tallerwebi.dominio.servicios.ServicioUsuario;
import com.tallerwebi.presentacion.dto.UsuarioProvDTO;
import com.tallerwebi.presentacion.dto.UsuarioSesionDto;

@Controller
@RequestMapping("/admin")
public class ControladorAdministrador {

    private ServicioAdministrador servicioAdmin;
    private ServicioProveedorI servicioProveedor;
    private ServicioClienteI servicioCliente;
    private ServicioUsuario servicioUsuario; 
    private ServicioEmail servicioEmail;

    private ServletContext servletContext; // rutas relativas

    @Autowired
    public ControladorAdministrador(ServicioAdministrador servicioAdmin, ServicioProveedorI servicioProveedor,
            ServicioEmail servicioEmail, ServletContext servletContext, ServicioClienteI servicioCliente, ServicioUsuario servicioUsuario) {
                
        this.servicioAdmin = servicioAdmin;
        this.servicioProveedor = servicioProveedor;
        this.servicioEmail = servicioEmail;
        this.servletContext = servletContext;//para ver el pdf de afip
        this.servicioCliente = servicioCliente;
        this.servicioUsuario = servicioUsuario;

    }

    @GetMapping("/dashboard-admin")
    public ModelAndView mostrarDashboard(HttpServletRequest request) {
        ModelMap modelMap = new ModelMap();

        UsuarioSesionDto usuarioSesionDto = (UsuarioSesionDto) request.getSession().getAttribute("usuarioLogueado");
        String rol_admin = "ADMIN";

        if (usuarioSesionDto == null || !rol_admin.equalsIgnoreCase(usuarioSesionDto.getRol())) {
            return new ModelAndView("redirect:/login");
        }

        modelMap.put("mailAdmin", usuarioSesionDto.getUsername());

        modelMap.put("totalUsuarios", servicioUsuario.contarUsuarios());
        modelMap.put("totalProveedores", servicioProveedor.contarProveedores(null));
        modelMap.put("proveedoresPendientes", servicioProveedor.contarProveedores(EstadoUsuario.PENDIENTE));
        modelMap.put("proveedoresRechazados", servicioProveedor.contarProveedores(EstadoUsuario.RECHAZADO));
        modelMap.put("totalClientes", servicioCliente.contarClientes());

        // lista combinada de clientes y proveedores
        modelMap.put("usuarios", servicioUsuario.obtenerTodosLosUsuarios());

        return new ModelAndView("dashboard-admin", modelMap);
    }

    @GetMapping("/proveedores-pendientes")
    public ModelAndView mostrarProveedoresPendientes(HttpServletRequest request) {
        ModelMap modelMap = new ModelMap();

        UsuarioSesionDto usuarioSesionDto = (UsuarioSesionDto) request.getSession().getAttribute("usuarioLogueado");
        String rol_admin = "ADMIN";

        if (usuarioSesionDto == null || !rol_admin.equalsIgnoreCase(usuarioSesionDto.getRol())) {
            return new ModelAndView("redirect:/login");
        }
        modelMap.put("mailAdmin", usuarioSesionDto.getUsername());

        List<Proveedor> proveedoresPendientes = servicioProveedor.obtenerTodosLosProveedoresPendientes();

        if (proveedoresPendientes == null) {
            modelMap.put("mensaje", "No hay proveedores pendientes.");
            return new ModelAndView("proveedores-pendientes", modelMap);
        }

        List<UsuarioProvDTO> proveedoresDTOs = obtenerProveedoresDtos(proveedoresPendientes);

        modelMap.put("proveedoresPendientes", proveedoresDTOs);

        return new ModelAndView("proveedores-pendientes", modelMap);
    }

    private List<UsuarioProvDTO> obtenerProveedoresDtos(List<Proveedor> proveedoresPendientes) {
        List<UsuarioProvDTO> proveedoresDTOs = new ArrayList<>();

        for (Proveedor proveedor : proveedoresPendientes) {
            UsuarioProvDTO dto = new UsuarioProvDTO();
            dto.setId(proveedor.getId());
            dto.setRazonSocial(proveedor.getRazonSocial());
            dto.setEmail(proveedor.getEmail());
            dto.setCuit(proveedor.getCuit());
            dto.setEstado(proveedor.getEstado());
            dto.setDocumentoPath(proveedor.getDocumento());
            proveedoresDTOs.add(dto);
        }
        return proveedoresDTOs;
    }

    @PostMapping("/cambiar-estado-proveedor")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> cambiarEstadoProveedor(@RequestBody Map<String, String> datos) {

        Map<String, Object> respuesta = new HashMap<>();

        Long id = Long.valueOf(datos.get("id"));
        String nuevoEstado = datos.get("nuevoEstado");

        Proveedor proveedor = servicioProveedor.buscarPorId(id);

        if (proveedor == null) {
            respuesta.put("error", "Proveedor no encontrado.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
        }

        proveedor.setEstado(EstadoUsuario.valueOf(nuevoEstado));
        servicioProveedor.actualizar(proveedor);

        // Enviar el email
        String asunto = "Estado de tu cuenta de proveedor en Ventanet";
        String mensaje;

        if ("ACTIVO".equals(nuevoEstado)) {
            mensaje = "Hola " + proveedor.getRazonSocial() + ",\n\n" +
                    "Nos complace informarte que tu cuenta como proveedor ha sido activada exitosamente. " +
                    "Ya puedes iniciar sesión y acceder a tu perfil para comenzar a utilizar nuestros servicios.\n\n" +
                    "Gracias por unirte a nuestra plataforma.\n\n" +
                    "Saludos cordiales,\n" +
                    "El equipo de Ventanet";
        } else {
            mensaje = "Hola " + proveedor.getRazonSocial() + ",\n\n" +
                    "Lamentamos informarte que tu cuenta como proveedor ha sido rechazada. " +
                    "Si tienes alguna pregunta o necesitas más información, no dudes en contactarnos.\n\n" +
                    "Gracias por tu interés en nuestra plataforma.\n\n" +
                    "Saludos cordiales,\n" +
                    "El equipo de Ventanet";
        }

        servicioEmail.enviarEmail(proveedor.getEmail(), asunto, mensaje, false);

        respuesta.put("mensaje", "Proveedor " + nuevoEstado.toLowerCase() + " correctamente.");
        respuesta.put("estado", nuevoEstado);
        respuesta.put("proveedorId", id);

        return ResponseEntity.ok(respuesta);
        /*
         * //consultamos lista de proveedores pendientes actualizada
         * List<Proveedor> proveedoresPendientes =
         * servicioProveedor.obtenerTodosLosProveedoresPendientes();
         * List<UsuarioProvDTO> proveedoresDTOs =
         * obtenerProveedoresDtos(proveedoresPendientes);
         * modelMap.put(mensaje, "Proveedor" + nuevoEstado.toLowerCase() +
         * " correctamente.");
         * modelMap.put("proveedoresPendientes", proveedoresDTOs);
         * 
         * return new ModelAndView("proveedores-pendientes", modelMap);
         */
    }

    @GetMapping("/ver-documento-proveedor/{id}")
    public void verDocumentoProveedor(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response) {

        Proveedor proveedor = servicioProveedor.buscarPorId(id);

        if (proveedor == null || proveedor.getDocumento() == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String docPath = proveedor.getDocumento();

        // Eliminamos la barra inicial si existe
        if (docPath.startsWith("/")) {
            docPath = docPath.substring(1);
        }

        // carpeta relativa donde se guardan los documentos de AFIP
        String uploadsAbsPath = servletContext.getRealPath("/resources/core/"); // convertir ruta relativa a ruta
                                                                                // absoluta

        if (uploadsAbsPath == null) {

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        // Construimos la ruta física dentro de webapp/resources/core/

        Path file = Paths.get(uploadsAbsPath).resolve(docPath).toAbsolutePath();// convierto absoluta

        System.out.println("Ruta final resuelta: " + file);

        if (!Files.exists(file) || !Files.isReadable(file)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // Determinar el tipo de contenido del archivo para que el navegador lo maneje
        // correctamente
        String fname = proveedor.getDocumento().toLowerCase();
        String contentType = "application/octet-stream"; // valor por defecto
        if (fname.endsWith(".pdf")) {
            contentType = "application/pdf";
        } else if (fname.endsWith(".jpg") || fname.endsWith(".jpeg")) {
            contentType = "image/jpeg";
        } else if (fname.endsWith(".png"))
            contentType = "image/png";

        response.setContentType(contentType);

        // Cabeceras para evitar bloqueo
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");

        String safeName = Paths.get(docPath).getFileName().toString();
        response.setHeader("Content-Disposition", "inline; filename=\"" + safeName + "\"");

        try {
            Files.copy(file, response.getOutputStream());
            response.getOutputStream().flush();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

    }

}

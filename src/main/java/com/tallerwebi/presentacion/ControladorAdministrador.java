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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tallerwebi.dominio.entidades.Proveedor;
import com.tallerwebi.dominio.enums.EstadoUsuario;
import com.tallerwebi.dominio.servicios.ServicioAdministrador;
import com.tallerwebi.dominio.servicios.ServicioClienteI;
import com.tallerwebi.dominio.servicios.ServicioEmail;
import com.tallerwebi.dominio.servicios.ServicioProveedorI;
import com.tallerwebi.dominio.servicios.ServicioUsuario;
import com.tallerwebi.presentacion.dto.UsuarioProvDTO;
import com.tallerwebi.presentacion.dto.UsuarioSesionDto;
import com.tallerwebi.presentacion.dto.UsuarioAdminDTO;

// Para el Excel
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.IOException;

// Para graficos
import java.util.LinkedHashMap;
import java.util.stream.Collectors;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import com.tallerwebi.dominio.entidades.Cotizacion;
import com.tallerwebi.dominio.enums.Rubro;
import com.tallerwebi.presentacion.dto.ProductoMasCotizadoDTO;
import com.tallerwebi.dominio.servicios.ServicioEstadisticas;
import com.tallerwebi.dominio.servicios.ServicioCotizacion;

@Controller
@RequestMapping("/admin")
public class ControladorAdministrador {

    private ServicioAdministrador servicioAdmin;
    private ServicioProveedorI servicioProveedor;
    private ServicioClienteI servicioCliente;
    private ServicioUsuario servicioUsuario;
    private ServicioEmail servicioEmail;
    private ServicioEstadisticas servicioEstadisticas;
    private ServicioCotizacion servicioCotizacion;

    private ServletContext servletContext; // rutas relativas

    @Autowired
    public ControladorAdministrador(ServicioAdministrador servicioAdmin, ServicioProveedorI servicioProveedor, ServicioEmail servicioEmail, ServletContext servletContext, ServicioClienteI servicioCliente, ServicioUsuario servicioUsuario, ServicioEstadisticas servicioEstadisticas, ServicioCotizacion servicioCotizacion) {
        this.servicioAdmin = servicioAdmin;
        this.servicioProveedor = servicioProveedor;
        this.servicioEmail = servicioEmail;
        this.servletContext = servletContext;// para ver el pdf de afip
        this.servicioCliente = servicioCliente;
        this.servicioUsuario = servicioUsuario;
        this.servicioEstadisticas = servicioEstadisticas;
        this.servicioCotizacion = servicioCotizacion;
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

        List<UsuarioAdminDTO> usuariosDTO = servicioUsuario.obtenerUsuariosParaAdmin();
        modelMap.put("usuarios", usuariosDTO);

        try {
            ObjectMapper mapper = new ObjectMapper();
            String usuariosJson = mapper.writeValueAsString(usuariosDTO);
            modelMap.put("usuariosJson", usuariosJson);
        } catch (Exception e) {
            modelMap.put("usuariosJson", "[]");
        }

        // --- Cantidad clientes / proveedores ---
        long totalClientes = servicioCliente.contarClientes();
        long totalProveedores = servicioProveedor.contarProveedores(null);

        modelMap.put("g_totalClientes", totalClientes);
        modelMap.put("g_totalProveedores", totalProveedores);
/* 
        // --- Top 5 productos más cotizados ---
        List<ProductoMasCotizadoDTO> topProductos =
                servicioEstadisticas.obtenerTopProductos(5);
        modelMap.put("topProductos", new Gson().toJson(topProductos));

        // --- Proveedores con más productos (top 5) ---
        List<Proveedor> proveedores = servicioProveedor.obtenerTodosLosProveedoresActivos();

        List<Map<String, Object>> topProveedores = proveedores.stream()
                .sorted((a, b) -> Integer.compare(b.getProductos().size(), a.getProductos().size()))
                .limit(5)
                .map(p -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("nombre", p.getNombreMostrable());
                    map.put("cantidad", p.getProductos().size());
                    return map;
                }).collect(Collectors.toList());

        modelMap.put("topProveedores", new Gson().toJson(topProveedores));
*/
        // --- Cotizaciones por estado ---
        List<Cotizacion> cotizaciones = servicioCotizacion.obtenerTodas();

        Map<String, Long> estados = new LinkedHashMap<>();
        estados.put("Aprobadas", cotizaciones.stream().filter(c -> c.getEstado().equals("APROBADA")).count());
        estados.put("Pendientes", cotizaciones.stream().filter(c -> c.getEstado().equals("PENDIENTE")).count());
        estados.put("Rechazadas", cotizaciones.stream().filter(c -> c.getEstado().equals("RECHAZADA")).count());

        modelMap.put("cotizacionesEstado", new Gson().toJson(estados));

        // --- Proveedores por rubro ---
        List<Rubro> rubros = servicioProveedor.obtenerRubrosActivos();

        List<Map<String, Object>> proveedoresPorRubro = rubros.stream()
                .map(r -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("rubro", r.getDescripcion());
                    m.put("cantidad", servicioProveedor.obtenerProveedoresPorRubro(r).size());
                    return m;
                }).collect(Collectors.toList());

        modelMap.put("proveedoresPorRubro", new Gson().toJson(proveedoresPorRubro));
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

    @GetMapping("/usuarios/exportar-excel")
    public void exportarUsuariosExcel(HttpServletResponse response) throws IOException {

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=usuarios.xlsx");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Usuarios");

        CellStyle estiloFecha = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        estiloFecha.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy HH:mm"));

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("Nombre");
        header.createCell(2).setCellValue("Email");
        header.createCell(3).setCellValue("CUIT");
        header.createCell(4).setCellValue("Rol");
        header.createCell(5).setCellValue("Estado");
        header.createCell(6).setCellValue("Fecha Alta");

        List<UsuarioAdminDTO> usuarios = servicioUsuario.obtenerUsuariosParaAdmin();

        int fila = 1;

        for (UsuarioAdminDTO u : usuarios) {
            Row row = sheet.createRow(fila++);

            row.createCell(0).setCellValue(u.getId());
            row.createCell(1).setCellValue(u.getNombreMostrable());
            row.createCell(2).setCellValue(u.getEmail());
            row.createCell(3).setCellValue(u.getCuit());
            row.createCell(4).setCellValue(u.getRol());
            row.createCell(5).setCellValue(u.getEstado().getDescripcion());

            Cell celdaFecha = row.createCell(6);
            celdaFecha.setCellValue(u.getFechaCreacion());
            celdaFecha.setCellStyle(estiloFecha);
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }

}
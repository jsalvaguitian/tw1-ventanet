package com.tallerwebi.presentacion;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tallerwebi.dominio.entidades.Producto;
import com.tallerwebi.dominio.entidades.Proveedor;
import com.tallerwebi.dominio.excepcion.NoHayProductoExistente;
import com.tallerwebi.dominio.excepcion.ProductoExistente;
import com.tallerwebi.dominio.servicios.ServicioMarca;
import com.tallerwebi.dominio.servicios.ServicioPresentacion;
import com.tallerwebi.dominio.servicios.ServicioProducto;
import com.tallerwebi.dominio.servicios.ServicioProveedorI;
import com.tallerwebi.dominio.servicios.ServicioTipoProducto;
import com.tallerwebi.presentacion.dto.UsuarioSesionDto;

@Controller
@RequestMapping("/producto")
public class ControladorProducto implements ServletContextAware {
    private ServicioProducto servicioProducto;
    private ServletContext servletContext;
    private final ServicioTipoProducto servicioTipoProducto;
    private final ServicioMarca servicioMarca;
    private final ServicioPresentacion servicioPresentacion;
    private final ServicioProveedorI servicioProveedor;

    @Autowired
    public ControladorProducto(ServicioProducto servicioProducto, ServicioTipoProducto servicioTipoProducto,
            ServicioMarca servicioMarca, ServicioPresentacion servicioPresentacion,
            ServicioProveedorI servicioProveedor) {
        new ArrayList<>();
        this.servicioProducto = servicioProducto;
        this.servicioMarca = servicioMarca;
        this.servicioTipoProducto = servicioTipoProducto;
        this.servicioPresentacion = servicioPresentacion;
        this.servicioProveedor = servicioProveedor;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @RequestMapping(path = "/nuevo-producto", method = RequestMethod.GET)
    public ModelAndView nuevoProducto(HttpServletRequest request) {
        UsuarioSesionDto usuarioSesion = (UsuarioSesionDto) request.getSession().getAttribute("usuarioLogueado");
        String rol_proveedor = "PROVEEDOR";

        if (usuarioSesion == null || !rol_proveedor.equalsIgnoreCase(usuarioSesion.getRol())) {
            return new ModelAndView("redirect:/login");
        }

        ModelMap model = new ModelMap();
        Producto producto = new Producto();
        model.put("producto", producto);
        Proveedor proveedor = servicioProveedor.obtenerPorIdUsuario(usuarioSesion.getId());
        producto.setProveedor(proveedor);
        model.put("tiposProducto", servicioTipoProducto.obtener());
        model.put("marcas", servicioMarca.obtener());
        model.put("presentaciones", servicioPresentacion.obtener());

        return new ModelAndView("nuevo-producto", model);
    }

    @RequestMapping(path = "/listado", method = RequestMethod.GET)
    public ModelAndView mostrarProductos(HttpServletRequest request) {

        ModelMap modelo = new ModelMap();

        try {
            UsuarioSesionDto usuarioSesion = (UsuarioSesionDto) request.getSession().getAttribute("usuarioLogueado");
            String rol_proveedor = "PROVEEDOR";

            if (usuarioSesion == null || !rol_proveedor.equalsIgnoreCase(usuarioSesion.getRol())) {
                return new ModelAndView("redirect:/login");
            }

            Proveedor proveedor = servicioProveedor.obtenerPorIdUsuario(usuarioSesion.getId());

            List<Producto> productos = this.servicioProducto.buscarPorProveedorId(proveedor.getId());
            modelo.put("productos", productos);
            if (productos.isEmpty()) {
                modelo.put("error", "No hay Productos");
            } else {
                modelo.put("exito", "Hay productos.");
            }
        } catch (NoHayProductoExistente e) {
            modelo.put("productos", new ArrayList<>());
            modelo.put("error", "no hay productos");
        }

        return new ModelAndView("producto-listado", modelo);
    }

    @PostMapping("/crear")
    public ModelAndView crearProducto(@ModelAttribute Producto producto,
            @RequestParam(value = "imagenFile", required = false) MultipartFile imagenFile,
            HttpServletRequest request) {
        ModelMap model = new ModelMap();
        UsuarioSesionDto usuarioSesion = (UsuarioSesionDto) request.getSession().getAttribute("usuarioLogueado");
        String rol_proveedor = "PROVEEDOR";

        if (usuarioSesion == null || !rol_proveedor.equalsIgnoreCase(usuarioSesion.getRol())) {
            return new ModelAndView("redirect:/login");
        }
        try {
            Proveedor proveedor = servicioProveedor.obtenerPorIdUsuario(usuarioSesion.getId());
            producto.setProveedor(proveedor);

            if (imagenFile != null && !imagenFile.isEmpty()) {
                String uploadDirectory = servletContext.getRealPath("/resources/core/uploads/");
                Path uploadPath = Paths.get(uploadDirectory);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                // Ejemplo: guardar en disco
                Path filePath = Paths.get(uploadDirectory, imagenFile.getOriginalFilename());
                // Copia el archivo
                Files.copy(imagenFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                // Guardar la ruta en el producto
                producto.setImagenUrl("/resources/core/uploads/" + imagenFile.getOriginalFilename());
            }
            servicioProducto.crearProducto(producto);
        } catch (ProductoExistente e) {
            model.put("error", "El producto ya existe");
            return new ModelAndView("nuevo-producto", model);
        } catch (Exception e) {
            model.put("error", "Error al registrar el nuevo producto");
            return new ModelAndView("nuevo-producto", model);
        }
        return new ModelAndView("redirect:listado", model);
    }

    @RequestMapping("/editar/{id}")
    public ModelAndView mostrarFormularioEditarProducto(@PathVariable Long id) {

        Producto producto = servicioProducto.obtenerPorId(id);
        ModelMap model = new ModelMap();
        model.put("producto", producto);
        model.put("tiposProducto", servicioTipoProducto.obtener());
        model.put("marcas", servicioMarca.obtener());
        model.put("presentaciones", servicioPresentacion.obtener());

        return new ModelAndView("editar-producto", model);
    }

    @PostMapping("/editar/{id}")
    public ModelAndView editarProducto(@PathVariable Long id,
            @ModelAttribute("producto") Producto producto,
            @RequestParam(value = "imagenFile", required = false) MultipartFile imagenFile,
            RedirectAttributes redirectAttributes, HttpServletRequest request) {
        ModelMap model = new ModelMap();
        try {
            UsuarioSesionDto usuarioSesion = (UsuarioSesionDto) request.getSession().getAttribute("usuarioLogueado");
            String rol_proveedor = "PROVEEDOR";

            if (usuarioSesion == null || !rol_proveedor.equalsIgnoreCase(usuarioSesion.getRol())) {
                return new ModelAndView("redirect:/login");
            }

            Proveedor proveedor = servicioProveedor.obtenerPorIdUsuario(usuarioSesion.getId());
            producto.setProveedor(proveedor);

            if (imagenFile != null && !imagenFile.isEmpty()) {
                String uploadDirectory = servletContext.getRealPath("/resources/core/uploads/");
                Path uploadPath = Paths.get(uploadDirectory);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                // Ejemplo: guardar en disco
                Path filePath = Paths.get(uploadDirectory, imagenFile.getOriginalFilename());
                // Copia el archivo
                Files.copy(imagenFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                // Guardar la ruta en el producto
                producto.setImagenUrl("/resources/core/uploads/" + imagenFile.getOriginalFilename());
            }
            
            servicioProducto.actualizar(producto);
            model.put("exito", "El producto se modificó con éxito ✅");
            model.put("producto", producto);
            model.put("tiposProducto", servicioTipoProducto.obtener());
            model.put("marcas", servicioMarca.obtener());
            model.put("presentaciones", servicioPresentacion.obtener());
            return new ModelAndView("editar-producto", model);
        } catch (Exception e) {
            model.put("error", "Error al actualizar producto: " + e.getMessage());
            return new ModelAndView("editar-producto", model);
        }
    }

    @RequestMapping("/eliminar/{id}")
    public String eliminarProducto(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        try {
            servicioProducto.eliminar(id);
            redirectAttrs.addFlashAttribute("exito", "Producto eliminado con éxito");
        } catch (NoHayProductoExistente e) {
            redirectAttrs.addFlashAttribute("error", "Producto no encontrado");
        }
        return "redirect:/producto/listado";
    }
}

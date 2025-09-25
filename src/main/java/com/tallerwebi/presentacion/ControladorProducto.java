package com.tallerwebi.presentacion;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
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
import com.tallerwebi.dominio.excepcion.NoHayProductoExistente;
import com.tallerwebi.dominio.excepcion.ProductoExistente;
import com.tallerwebi.dominio.servicios.ServicioProducto;
import com.tallerwebi.presentacion.dto.ProductoDTO;

@Controller
@RequestMapping("/producto")
public class ControladorProducto implements ServletContextAware {
    private List<ProductoDTO> productos;
    private ServicioProducto servicioProducto;
    private ServletContext servletContext;

    public ControladorProducto(ServicioProducto servicioProducto) {
        this.productos = new ArrayList<>();
        this.servicioProducto = servicioProducto;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @RequestMapping(path = "/nuevo-producto", method = RequestMethod.GET)
    public ModelAndView nuevoProducto() {
        ModelMap model = new ModelMap();
        model.put("producto", new Producto());
        return new ModelAndView("nuevo-producto", model);
    }

    @RequestMapping(path = "/listado", method = RequestMethod.GET)
    public ModelAndView mostrarProductos() {

        ModelMap modelo = new ModelMap();

        try {
            List<Producto> productos = this.servicioProducto.obtener();
            modelo.put("productos", productos);
            modelo.put("exito", "Hay productos.");
        } catch (NoHayProductoExistente e) {
            modelo.put("productos", new ArrayList<>());
            modelo.put("error", "no hay productos");
        }

        return new ModelAndView("producto-listado", modelo);
    }

    @PostMapping("/crear")
    public ModelAndView crearProducto(@ModelAttribute Producto producto,
            @RequestParam(value ="imagenFile", required = false) MultipartFile imagenFile) {
        ModelMap model = new ModelMap();
        try {
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
        ModelAndView mav = new ModelAndView("editar-producto");
        Producto producto = servicioProducto.obtenerPorId(id);
        mav.addObject("producto", producto);
        return mav;
    }

    @PostMapping("/editar/{id}")
    public ModelAndView editarProducto(@PathVariable Long id,
                                 @ModelAttribute("producto") Producto producto,
                                 @RequestParam(value = "imagenFile", required = false) MultipartFile imagenFile,
                                 RedirectAttributes redirectAttributes) {
                                    ModelMap model = new ModelMap();
        try {
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
            producto.setId(id); // aseguramos que edite el producto correcto
            servicioProducto.actualizar(producto);
            redirectAttributes.addFlashAttribute("exito", "Producto actualizado correctamente");
            model.put("exito", "Producto editado correctamente");
            return new ModelAndView("redirect:/producto/listado", model);
            //return "redirect:/producto/listar";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar producto: " + e.getMessage());
            model.put("error", "El producto ya existe");
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

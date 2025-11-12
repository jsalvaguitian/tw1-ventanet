package com.tallerwebi.presentacion;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;

import com.tallerwebi.dominio.excepcion.CloudinaryException;
import com.tallerwebi.dominio.excepcion.ExcelGeneradorException;
import com.tallerwebi.dominio.excepcion.ExcelReaderException;
import com.tallerwebi.dominio.servicios.ServicioFileStorage;
import com.tallerwebi.dominio.servicios.ServicioImportacionProduct;
import com.tallerwebi.dominio.servicios.ServicioTablas;
import com.tallerwebi.dominio.utils.ExcelGenerator;
import com.tallerwebi.presentacion.dto.ProductoImportDTO;
import com.tallerwebi.presentacion.dto.ProductosWrapper;
import com.tallerwebi.presentacion.dto.UsuarioSesionDto;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/importacion-productos")
public class ControladorImportacionProd {

    private ExcelGenerator excelGenerator;
    private ServicioImportacionProduct servicioImportacionProduct;
    private ServicioTablas servicioTablas;
    private ServicioFileStorage servicioFileStorage;

    @Autowired
    public ControladorImportacionProd(ServicioFileStorage servicioFileStorage, ExcelGenerator excelGenerator,
            ServicioImportacionProduct servicioImportacionProduct, ServicioTablas servicioTablas) {
        this.excelGenerator = excelGenerator;
        this.servicioImportacionProduct = servicioImportacionProduct;
        this.servicioTablas = servicioTablas;
        this.servicioFileStorage = servicioFileStorage;

    }

    @GetMapping()
    public ModelAndView mostrarImportacionProductos(HttpServletRequest request) {
        if (usuarioNoAutorizado(request, "PROVEEDOR")) {
            return new ModelAndView("redirect:/login");
        }

        ModelMap modelo = new ModelMap();
        modelo.put("colores", servicioTablas.obtenerColores());
        modelo.put("marcas", servicioTablas.obtenerMarcas());
        modelo.put("tiposProducto", servicioTablas.obtenerTipoProductos());
        modelo.put("alturas", servicioTablas.obtenerAltos());
        modelo.put("anchos", servicioTablas.obtenerAnchos());
        modelo.put("materiales", servicioTablas.obtenerMateriales());
        modelo.put("tiposVidrios", servicioTablas.obtenerTipoDeVidrios());
        modelo.put("tiposProductos", servicioTablas.obtenerTipoProductos());
        modelo.put("presentaciones", servicioTablas.obtenePresentaciones());

        return new ModelAndView("importacion-productos", modelo);
    }

    @GetMapping("/descargar-excel")
    public void descargarPlantillaExcel(HttpServletResponse response, HttpServletRequest request) throws IOException {
        if (usuarioNoAutorizado(request, "PROVEEDOR")) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        try {
            byte[] excel = excelGenerator.generarPlantillaProductos();

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=plantilla_productos.xlsx");
            response.getOutputStream().write(excel);
        } catch (ExcelGeneradorException exception) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exception.getMessage());

        }
    }

    @PostMapping("/subir-excel")
    public ModelAndView procesarExcel(@RequestParam("archivoExcel") MultipartFile archivoExcel,

            @RequestParam("imagenes") List<MultipartFile> imagenes, HttpServletRequest request) {

        if (usuarioNoAutorizado(request, "PROVEEDOR")) {
            return new ModelAndView("redirect:/login");
        }
        ModelMap modelo = new ModelMap();
        String sessionId = request.getSession().getId();

        modelo.put("colores", servicioTablas.obtenerColores());
        modelo.put("marcas", servicioTablas.obtenerMarcas());
        modelo.put("tiposProducto", servicioTablas.obtenerTipoProductos());
        modelo.put("alturas", servicioTablas.obtenerAltos());
        modelo.put("anchos", servicioTablas.obtenerAnchos());
        modelo.put("materiales", servicioTablas.obtenerMateriales());
        modelo.put("tiposVidrios", servicioTablas.obtenerTipoDeVidrios());
        modelo.put("tiposProductos", servicioTablas.obtenerTipoProductos());
        modelo.put("presentaciones", servicioTablas.obtenePresentaciones());

        if (archivoExcel.isEmpty() || imagenes.isEmpty()) {
            modelo.put("error", "Debe seleccionar un archivo excel y subir imagenes de los productos.");
            return new ModelAndView("importacion-productos", modelo);
        }

        try {

            // guardar imagenes temporalmente para la proxima request
            List<String> rutasTemporales = new ArrayList<>();

            for (MultipartFile imagen : imagenes) {
                String ruta = servicioFileStorage.guardarArchivoTemporal(imagen, sessionId);
                rutasTemporales.add(ruta);
            }

            List<ProductoImportDTO> productoImportDTOs = servicioImportacionProduct.leerYValidar(archivoExcel,
                    imagenes);

            // Crear un mapa que relacione el nombre original de la imagen (sin extensión,
            // en minúscula) con la ruta temporal
            Map<String, String> nombreImagenARuta = new HashMap<>();
            for (String ruta : rutasTemporales) {
                File f = new File(ruta);
                String nombreArchivo = f.getName(); // ej: 4bd938f6-ce1d-4c07-b870-eaa05ffd6846_piso3.jpg

                // Extraer la parte después del UUID y guion bajo
                int guion = nombreArchivo.indexOf("_");
                if (guion > 0) {
                    nombreArchivo = nombreArchivo.substring(guion + 1); // piso3.jpg
                }

                // Quitar extensión
                int p = nombreArchivo.lastIndexOf(".");
                if (p > 0) {
                    nombreArchivo = nombreArchivo.substring(0, p); // piso3
                }

                nombreImagenARuta.put(nombreArchivo.trim().toLowerCase(), ruta);
            }

            // Asignar las rutas ordenadas según el Excel
            List<String> rutasTemporalesOrdenadas = new ArrayList<>();
            for (ProductoImportDTO dto : productoImportDTOs) {
                String nombreExcel = dto.getNombreImagen();
                if (nombreExcel != null && !nombreExcel.isEmpty()) {
                    int p = nombreExcel.lastIndexOf(".");
                    if (p > 0) {
                        nombreExcel = nombreExcel.substring(0, p); // sin extensión
                    }

                    String ruta = nombreImagenARuta.get(nombreExcel.trim().toLowerCase());
                    if (ruta != null) {
                        rutasTemporalesOrdenadas.add(ruta);
                    } else {
                        // Fallback: si no se encuentra imagen, agregar null o ruta genérica
                        rutasTemporalesOrdenadas.add("/spring/resources/core/imagen-no-disponible.png");
                    }
                } else {
                    // Fallback si nombreExcel es null o vacío
                    rutasTemporalesOrdenadas.add("/spring/resources/core/imagen-no-disponible.png");
                }
            }

            ProductosWrapper productosWrapper = new ProductosWrapper();
            productosWrapper.setProductos(productoImportDTOs);

            modelo.put("productosWrapper", productosWrapper);
            modelo.put("imagenesTemporales", rutasTemporalesOrdenadas);
            modelo.put("sessionId", sessionId);
            request.getSession().setAttribute("productosAImportar_" + sessionId, productoImportDTOs);

            // Crear productosMapeados para el formulario Thymeleaf
            List<Map<String, Object>> productosMapeados = productoImportDTOs.stream()
                    .map(p -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("nombre", p.getNombre());
                        map.put("precio", p.getPrecio());
                        map.put("stock", p.getStock());
                        map.put("nombreImagen", p.getNombreImagen());
                        map.put("descripcion", p.getDescripcion());
                        map.put("marca", p.getMarca());
                        map.put("alto", p.getAlto());
                        map.put("ancho", p.getAncho());
                        map.put("color", p.getColor());
                        map.put("material", p.getMaterial());
                        map.put("presentacion", p.getPresentacion());
                        map.put("tipoProducto", p.getTipoProducto());
                        map.put("subtipoProducto", p.getSubtipoProducto());
                        map.put("tipoVidrio", p.getTipoVidrio());
                        map.put("valido", p.getValido());
                        map.put("error", p.getError());
                        map.put("advertencia", p.getAdvertencia());
                        return map;
                    }).collect(Collectors.toList());

            modelo.put("productosMapeados", productosMapeados);

        } catch (ExcelReaderException e) {
            modelo.put("error", e.getMessage());
        } catch (Exception exp) {
            modelo.put("error", "Error al procesar archivos: " + exp.getMessage());
        }
        return new ModelAndView("importacion-productos", modelo);
    }

    @PostMapping("/confirmar")
    public ModelAndView confirmarImportacion(@RequestParam("imagenesTemporales") String[] imagenesTemporalesArray,
            @RequestParam("sessionId") String sessionId,
            RedirectAttributes redirectAttrs,
            HttpServletRequest request) {

        if (usuarioNoAutorizado(request, "PROVEEDOR")) {
            return new ModelAndView("redirect:/login");
        }
        List<String> imagenesTemporales = Arrays.asList(imagenesTemporalesArray); 

        List<ProductoImportDTO> productosAImportar = (List<ProductoImportDTO>) request.getSession().getAttribute("productosAImportar_" + sessionId);

        if (productosAImportar == null || productosAImportar.isEmpty()) {
            // Manejar error si la sesión expiró o no se encontró
            redirectAttrs.addFlashAttribute("error", "Error de sesión. Vuelva a subir el archivo.");
            return new ModelAndView("redirect:/importacion-productos");
        }

        ModelMap modelo = new ModelMap();

        UsuarioSesionDto usuarioSesion = (UsuarioSesionDto) request.getSession().getAttribute("usuarioLogueado");

        Long proveedorId = usuarioSesion.getId();

        modelo.put("colores", servicioTablas.obtenerColores());
        modelo.put("marcas", servicioTablas.obtenerMarcas());
        modelo.put("tiposProducto", servicioTablas.obtenerTipoProductos());
        modelo.put("alturas", servicioTablas.obtenerAltos());
        modelo.put("anchos", servicioTablas.obtenerAnchos());
        modelo.put("materiales", servicioTablas.obtenerMateriales());
        modelo.put("tiposVidrios", servicioTablas.obtenerTipoDeVidrios());
        modelo.put("tiposProductos", servicioTablas.obtenerTipoProductos());
        modelo.put("presentaciones", servicioTablas.obtenePresentaciones());

        try {
            List<File> archivosLocales = imagenesTemporales.stream()
                    .map(r -> new File(System.getProperty("user.dir") + "/src/main/webapp/resources/core" + r))
                    .collect(Collectors.toList());

            servicioImportacionProduct.importar(productosAImportar, archivosLocales, proveedorId);
            servicioFileStorage.eliminarCarpetaTemporal("tmp/" + sessionId);
            redirectAttrs.addFlashAttribute("mensaje", "Productos importados correctamente.");

        } catch (CloudinaryException e) {
            redirectAttrs.addFlashAttribute("error", "Hubo un error en el guardado de las imagenes");

        } catch (IOException e) {
            redirectAttrs.addFlashAttribute("error", "Error al confirmar la importación: " + e.getMessage());
        }
        // redirigir para no reenviar el form

        return new ModelAndView("redirect:/importacion-productos", modelo);
    }

    //////////////////////////////////////////////////////////////////////////////
    private boolean usuarioNoAutorizado(HttpServletRequest request, String rolEsperado) {
        UsuarioSesionDto usuarioSesion = (UsuarioSesionDto) request.getSession().getAttribute("usuarioLogueado");
        return (usuarioSesion == null || !rolEsperado.equalsIgnoreCase(usuarioSesion.getRol()));
    }

}

/*
 * @PostMapping("/subir-excel")
 * public ModelAndView procesarExcel(@RequestParam("archivoExcel") MultipartFile
 * archivoExcel,
 * 
 * @RequestParam("imagenes") List<MultipartFile> imagenes,
 * HttpServletRequest request) {
 * 
 * if (usuarioNoAutorizado(request, "PROVEEDOR")) {
 * return new ModelAndView("redirect:/login");
 * }
 * 
 * ModelMap modelo = new ModelMap();
 * // cargar listas de referencia
 * modelo.put("colores", servicioTablas.obtenerColores());
 * modelo.put("marcas", servicioTablas.obtenerMarcas());
 * modelo.put("tiposProducto", servicioTablas.obtenerTipoProductos());
 * modelo.put("alturas", servicioTablas.obtenerAltos());
 * modelo.put("anchos", servicioTablas.obtenerAnchos());
 * modelo.put("materiales", servicioTablas.obtenerMateriales());
 * modelo.put("tiposVidrios", servicioTablas.obtenerTipoDeVidrios());
 * modelo.put("presentaciones", servicioTablas.obtenePresentaciones());
 * 
 * if (archivoExcel.isEmpty() || imagenes.isEmpty()) {
 * modelo.put("error",
 * "Debe seleccionar un archivo Excel y subir imágenes de los productos.");
 * return new ModelAndView("importacion-productos", modelo);
 * }
 * 
 * try {
 * String sessionId = request.getSession().getId();
 * 
 * // Guardar imágenes temporalmente
 * List<String> rutasTemporales = new ArrayList<>();
 * for (MultipartFile imagen : imagenes) {
 * String ruta = servicioFileStorage.guardarArchivoTemporal(imagen, sessionId);
 * rutasTemporales.add(ruta);
 * }
 * 
 * // Leer y validar Excel
 * List<ProductoImportDTO> productoImportDTOs =
 * servicioImportacionProduct.leerYValidar(archivoExcel,
 * imagenes);
 * 
 * // Crear mapa nombreImagen -> ruta
 * Map<String, String> mapaNombreImagenARuta = new HashMap<>();
 * for (String ruta : rutasTemporales) {
 * File f = new File(ruta);
 * String nombreArchivo = f.getName().toLowerCase(); // normalizado
 * mapaNombreImagenARuta.put(nombreArchivo, ruta);
 * }
 * 
 * ProductosWrapper productosWrapper = new ProductosWrapper();
 * productosWrapper.setProductos(productoImportDTOs);
 * 
 * modelo.put("productosWrapper", productosWrapper);
 * modelo.put("mapaNombreImagenARuta", mapaNombreImagenARuta);
 * modelo.put("sessionId", sessionId);
 * 
 * } catch (ExcelReaderException e) {
 * modelo.put("error", e.getMessage());
 * } catch (Exception exp) {
 * modelo.put("error", "Error al procesar archivos: " + exp.getMessage());
 * }
 * 
 * return new ModelAndView("importacion-productos", modelo);
 */

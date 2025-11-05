package com.tallerwebi.presentacion;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.tallerwebi.dominio.entidades.Marca;
import com.tallerwebi.dominio.entidades.Producto;
import com.tallerwebi.dominio.entidades.Proveedor;
import com.tallerwebi.dominio.entidades.TipoProducto;
import com.tallerwebi.dominio.entidades.TipoVentana;
import com.tallerwebi.dominio.enums.Rubro;
import com.tallerwebi.dominio.servicios.ServicioMarca;
import com.tallerwebi.dominio.servicios.ServicioProducto;
import com.tallerwebi.dominio.servicios.ServicioProveedorI;
import com.tallerwebi.dominio.servicios.ServicioTipoProducto;
import com.tallerwebi.presentacion.dto.ProductoDTO;
import com.tallerwebi.presentacion.dto.ProductoGenericoDTO;
import com.tallerwebi.presentacion.dto.UsuarioProvDTO;

import jakarta.mail.Session;

@Controller
public class ControladorCatalogo {

    private ServicioProducto servicioProducto;
    private ServicioProveedorI servicioProveedor;
    private ServicioTipoProducto servicioTipoProducto;
    private ServicioMarca servicioMarca;

    @Autowired
    public ControladorCatalogo(ServicioProducto servicioProducto, ServicioProveedorI servicioProveedor,
            ServicioTipoProducto servicioTipoProducto, ServicioMarca servicioMarca) {
        this.servicioProducto = servicioProducto;
        this.servicioProveedor = servicioProveedor;
        this.servicioTipoProducto = servicioTipoProducto;
        this.servicioMarca = servicioMarca;
    }

    @GetMapping("/menu-catalogo")
    public ModelAndView irAMenueneralCatalogo() {
        return new ModelAndView("menu-general-catalogo");
    }

    @GetMapping("/ver-proveedores")
    public ModelAndView verProveedores(HttpServletRequest request) {
        ModelMap modelMap = new ModelMap();
        List<Rubro> rubros = servicioProveedor.obtenerRubrosActivos();

        List<Proveedor> proveedores = servicioProveedor.obtenerTodosLosProveedoresActivos();
        List<UsuarioProvDTO> provDTOs = convertirProveedoresADtosFiltro(proveedores);

        modelMap.put("rubros", rubros);
        modelMap.put("proveedores", provDTOs);

        return new ModelAndView("ver-proveedores", modelMap);
    }

    @GetMapping("/proveedores/filtrar/{rubro}")
    @ResponseBody
    public List<UsuarioProvDTO> filtrarProveedoresPorRubro(@PathVariable Rubro rubro) {
        List<Proveedor> proveedores = servicioProveedor.obtenerProveedoresPorRubro(rubro);
        List<UsuarioProvDTO> provDTOs = convertirProveedoresADtosFiltro(proveedores);

        return provDTOs;
    }

    @GetMapping("/catalogo/{id}")
    public ModelAndView verCatalogoProveedor(@PathVariable("id") Long idProveedor,
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) Long tipoProductoId,
            @RequestParam(required = false) Long tipoVentanaId) {

        ModelMap modelMap = new ModelMap();

        Proveedor proveedor = servicioProveedor.buscarPorId(idProveedor);
        if (proveedor == null) {
            modelMap.put("mensaje", "No se encontro el proveedor. Lo sentimos");
            return new ModelAndView("catalogo-proveedor", modelMap);
        }

        // creo mi dto Proveedor
        UsuarioProvDTO dtoProveedor = new UsuarioProvDTO(idProveedor, proveedor.getRazonSocial(),
                proveedor.getLogoPath(), proveedor.getRubro());
        modelMap.put("proveedor", dtoProveedor);

        List<Producto> productos = servicioProducto.obtenerProductosFiltrados(idProveedor, busqueda, tipoProductoId,
                tipoVentanaId);

        // filtros
        List<TipoProducto> tiposProductos = servicioProducto.obtenerTiposProductos(idProveedor);
        List<TipoVentana> tipoVentanas = servicioProducto.obtenerTiposVentanas(idProveedor);

        if (productos == null || productos.isEmpty()) {
            modelMap.put("mensaje", "No hay productos cargados");
            return new ModelAndView("catalogo-proveedor", modelMap);
        }

        // creo mi lista de productos dtos
        List<ProductoDTO> productoDTOs = convertirProductosADtos(productos);
        modelMap.put("productos", productoDTOs);

        // filtro segun los producto que tenemos
        modelMap.put("tiposProducto", tiposProductos);

        modelMap.put("tiposVentana", tipoVentanas);

        // modelMap.put("busqueda", busqueda); //VERIFICAR si es necesario

        modelMap.put("idTipoProducto", tipoProductoId);

        modelMap.put("idTipoVentana", tipoVentanaId);

        return new ModelAndView("catalogo-proveedor", modelMap);
    }

    @GetMapping("/catalogo/{idProveedor}/detalle/{idProducto}")
    public ModelAndView verDetalleProducto(@PathVariable("idProveedor") Long idProveedor,
            @PathVariable("idProducto") Long idProducto) {
        ModelMap modelMap = new ModelMap();

        Proveedor proveedor = servicioProveedor.buscarPorId(idProveedor);

        if (proveedor == null) {
            modelMap.put("mensaje", "El proveedor no existe.");
            return new ModelAndView("detalle-producto", modelMap);
        }

        UsuarioProvDTO provDTO = new UsuarioProvDTO(idProveedor, proveedor.getRazonSocial(), proveedor.getLogoPath(),
                proveedor.getRubro());

        Producto producto = servicioProducto.obtenerPorId(idProducto);
        if (producto == null) {
            modelMap.put("mensaje", "El producto no existe.");
            return new ModelAndView("detalle-producto", modelMap);
        }

        modelMap.put("proveedor", provDTO);
        modelMap.put("producto", producto);

        return new ModelAndView("detalle-producto", modelMap);
    }

    // FLUJO 2: Explorar por productos semejantes
    // el detalle de producto sera como una comparacion
    // boton solicitar cotizacion
    // si el cliente no esta logueado solicitar q se loguee
    // si lo esta, se notifica N proveedores que tiene ese producto

    @GetMapping("/ver-productos")
    public ModelAndView verCatalogoPorProductosGenericos(){
        ModelMap modelMap = new ModelMap();
        List<ProductoGenericoDTO> productosGenericos = servicioProducto.obtenerProductosGenericos();
        modelMap.put("productosGenericos", productosGenericos);
        return new ModelAndView("catalogo-producto-generico", modelMap);

    }
    
    /*@GetMapping("/ver-productos")
    public ModelAndView verCatalogo(HttpServletRequest request,
            @RequestParam(required = false) Long proveedorId,
            @RequestParam(required = false) Long tipoProductoId) {

        ModelMap modelMap = new ModelMap();

        List<Proveedor> proveedores = servicioProveedor.obtenerTodosLosProveedoresActivos();
        List<UsuarioProvDTO> provFiltroDTOs = convertirProveedoresADtosFiltro(proveedores);

        List<TipoProducto> tiposProductos = servicioTipoProducto.obtener();

        modelMap.put("proveedores", provFiltroDTOs);
        modelMap.put("tiposProducto", tiposProductos);

        // List<Producto> productos = servicioProducto.obtener();// por el momento 15
        // productos tendra la
        List<Producto> productos = servicioProducto.buscarConFiltros(tipoProductoId);// despues agregar mas filtros

        // bbdd

        if (productos == null) {
            modelMap.put("mensaje", "No hay productos cargados");
            return new ModelAndView("catalogo", modelMap);
        }

        List<ProductoDTO> productoDTOs = convertirProductosADtos(productos);

        modelMap.put("productos", productoDTOs);

        return new ModelAndView("catalogo", modelMap);
        // luego preguntar si un cliente inicio sesion
    }*/

    // -------------------------------------------------------------------------------------------------
    private List<UsuarioProvDTO> convertirProveedoresADtosFiltro(List<Proveedor> proveedores) {
        List<UsuarioProvDTO> usuarioProvDTOs = new ArrayList<>();

        for (Proveedor uno : proveedores) {
            UsuarioProvDTO dtoProv = new UsuarioProvDTO(uno.getId(), uno.getRazonSocial(), uno.getLogoPath(),
                    uno.getRubro());// pido
            // loguito
            usuarioProvDTOs.add(dtoProv);
        }
        return usuarioProvDTOs;
    }

    private List<ProductoDTO> convertirProductosADtos(List<Producto> productos) {

        List<ProductoDTO> productoDTOs = new ArrayList<>();

        for (Producto uno : productos) {

            ProductoDTO productoDTO = new ProductoDTO(uno.getNombre(), uno.getDescripcion(), uno.getImagenUrl(),
                    uno.getTipoProducto().getId(), uno.getMarca().getId());

            productoDTO.setId(uno.getId());

            TipoProducto tipoProducto = servicioTipoProducto.obtenerPorId(uno.getTipoProducto().getId());

            Marca marca = servicioMarca.obtenerPorId(uno.getMarca().getId());

            productoDTO.setTipoProducto(tipoProducto.getNombre());

            productoDTO.setMarca(marca.getNombre());

            productoDTO.setPrecio(uno.getPrecio());

            productoDTOs.add(productoDTO);
        }
        return productoDTOs;

    }

}

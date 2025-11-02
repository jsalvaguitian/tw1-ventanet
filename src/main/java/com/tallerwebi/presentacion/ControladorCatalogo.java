package com.tallerwebi.presentacion;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.tallerwebi.dominio.entidades.Marca;
import com.tallerwebi.dominio.entidades.Producto;
import com.tallerwebi.dominio.entidades.Proveedor;
import com.tallerwebi.dominio.entidades.TipoProducto;
import com.tallerwebi.dominio.servicios.ServicioMarca;
import com.tallerwebi.dominio.servicios.ServicioProdV2;
import com.tallerwebi.dominio.servicios.ServicioProducto;
import com.tallerwebi.dominio.servicios.ServicioProveedorI;
import com.tallerwebi.dominio.servicios.ServicioTipoProducto;
import com.tallerwebi.presentacion.dto.ProductoDTO;
import com.tallerwebi.presentacion.dto.UsuarioProvDTO;

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
        List<Proveedor> proveedores = servicioProveedor.obtenerTodosLosProveedoresActivos();

        return new ModelAndView("ver-proveedores", modelMap);
    }
    

    @GetMapping("/ver-productos")
    public ModelAndView verCatalogo(HttpServletRequest request,
            @RequestParam(required = false) Long proveedorId,
            @RequestParam(required = false) Long tipoProductoId){

        ModelMap modelMap = new ModelMap();

        List<Proveedor> proveedores = servicioProveedor.obtenerTodosLosProveedoresActivos();
        List<UsuarioProvDTO> provFiltroDTOs = convertirProveedoresADtosFiltro(proveedores);

        List<TipoProducto> tiposProductos = servicioTipoProducto.obtener();

        modelMap.put("proveedores", provFiltroDTOs);
        modelMap.put("tiposProducto", tiposProductos);

        // List<Producto> productos = servicioProducto.obtener();// por el momento 15
        // productos tendra la
        List<Producto> productos = servicioProducto.buscarConFiltros(tipoProductoId);//despues agregar mas filtros

        // bbdd

        if (productos == null) {
            modelMap.put("mensaje", "No hay productos cargados");
            return new ModelAndView("catalogo", modelMap);
        }

        List<ProductoDTO> productoDTOs = convertirProductosADtos(productos);

        modelMap.put("productos", productoDTOs);

        return new ModelAndView("catalogo", modelMap);
        // luego preguntar si un cliente inicio sesion
    }

    private List<UsuarioProvDTO> convertirProveedoresADtosFiltro(List<Proveedor> proveedores) {
        List<UsuarioProvDTO> usuarioProvDTOs = new ArrayList<>();

        for (Proveedor uno : proveedores) {
            UsuarioProvDTO dtoProv = new UsuarioProvDTO(uno.getId(), uno.getRazonSocial());
            usuarioProvDTOs.add(dtoProv);
        }
        return usuarioProvDTOs;
    }

    private List<ProductoDTO> convertirProductosADtos(List<Producto> productos) {

        List<ProductoDTO> productoDTOs = new ArrayList<>();

        for (Producto uno : productos) {

            ProductoDTO productoDTO = new ProductoDTO(uno.getNombre(), uno.getDescripcion(), uno.getImagenUrl(),
                    uno.getTipoProducto().getId(), uno.getMarca().getId());
            TipoProducto tipoProducto = servicioTipoProducto.obtenerPorId(uno.getTipoProducto().getId());
            Marca marca = servicioMarca.obtenerPorId(uno.getMarca().getId());
            productoDTO.setTipoProducto(tipoProducto.getNombre());
            productoDTO.setMarca(marca.getNombre());
            productoDTOs.add(productoDTO);
        }
        return productoDTOs;

    }

}

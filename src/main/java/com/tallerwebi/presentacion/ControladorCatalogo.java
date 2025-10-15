package com.tallerwebi.presentacion;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.tallerwebi.dominio.entidades.Producto;
import com.tallerwebi.dominio.servicios.ServicioProdV2;
import com.tallerwebi.dominio.servicios.ServicioProveedorI;
import com.tallerwebi.presentacion.dto.CatalogoProductoDTO;

@Controller
public class ControladorCatalogo {

    private ServicioProdV2 servicioProducto; 
    private ServicioProveedorI servicioProveedor;

    @Autowired
    public ControladorCatalogo(ServicioProdV2 servicioProducto, ServicioProveedorI servicioProveedor) {
        this.servicioProducto = servicioProducto;
        this.servicioProveedor = servicioProveedor;
    }

    @GetMapping("/ver-catalogo")
    public ModelAndView verCatalogo(HttpServletRequest request) {

        ModelMap modelMap = new ModelMap();

        List<String>proveedorNombres = servicioProveedor.obtenerTodosLosNombresProveedores();
        List<String>tiposProductos = servicioProducto.obtenerTodosLosTiposProductos(); 

        modelMap.put("proveedoresFiltro", proveedorNombres);
        modelMap.put("tiposFiltro", tiposProductos);

        Set<Producto> productos = servicioProducto.obtenerTodosLosProductos();// por el momento 15 productos tendra la
                   
        // bbdd

        if (productos == null) {
            modelMap.put("mensaje", "No hay productos cargados");
            return new ModelAndView("ver-catalogo", modelMap);
        }

        List<CatalogoProductoDTO> productoDTOs = convertirProductosADtos(productos);

        modelMap.put("productos", productoDTOs);

        return new ModelAndView("ver-catalogo", modelMap);
        // luego preguntar si un cliente inicio sesion
    }

    private List<CatalogoProductoDTO> convertirProductosADtos(Set<Producto> productos) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'convertirProductosADtos'");
    }

}

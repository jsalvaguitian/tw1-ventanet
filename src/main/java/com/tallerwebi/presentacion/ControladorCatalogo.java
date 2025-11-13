package com.tallerwebi.presentacion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.tallerwebi.dominio.entidades.Alto;
import com.tallerwebi.dominio.entidades.Ancho;
import com.tallerwebi.dominio.entidades.CarritoCotizacion;
import com.tallerwebi.dominio.entidades.Color;
import com.tallerwebi.dominio.entidades.Marca;
import com.tallerwebi.dominio.entidades.MaterialDePerfil;
import com.tallerwebi.dominio.entidades.Producto;
import com.tallerwebi.dominio.entidades.Proveedor;
import com.tallerwebi.dominio.entidades.TipoProducto;
import com.tallerwebi.dominio.entidades.TipoVentana;
import com.tallerwebi.dominio.enums.Rubro;
import com.tallerwebi.dominio.excepcion.NoHayProductoExistente;
import com.tallerwebi.dominio.excepcion.ProveedorNoExistente;
import com.tallerwebi.dominio.excepcion.UsuarioInexistenteException;
import com.tallerwebi.dominio.servicios.ServicioMarca;
import com.tallerwebi.dominio.servicios.ServicioProducto;
import com.tallerwebi.dominio.servicios.ServicioProveedorI;
import com.tallerwebi.dominio.servicios.ServicioTablas;
import com.tallerwebi.dominio.servicios.ServicioTipoProducto;
import com.tallerwebi.dominio.servicios.ServicioTipoVentana;
import com.tallerwebi.dominio.servicios.ServicioUsuario;
import com.tallerwebi.presentacion.dto.ProductoDTO;
import com.tallerwebi.presentacion.dto.UsuarioProvDTO;
import com.tallerwebi.presentacion.dto.UsuarioSesionDto;
import com.tallerwebi.dominio.servicios.ServicioCarritoCoti;
import com.tallerwebi.dominio.servicios.ServicioClienteI;
import com.tallerwebi.dominio.servicios.ServicioCotizacion;
import com.tallerwebi.dominio.entidades.Cliente;

@Controller
public class ControladorCatalogo {

    private ServicioProducto servicioProducto;
    private ServicioProveedorI servicioProveedor;
    private ServicioTipoProducto servicioTipoProducto;
    private ServicioMarca servicioMarca;
    private ServicioTipoVentana servicioTipoVentana;
    private ServicioTablas servicioTablas;
    private ServicioCotizacion servicioCotizacion;
    private ServicioClienteI clienteService;
    private ServicioCarritoCoti carritoService;

    @Autowired
    public ControladorCatalogo(ServicioProducto servicioProducto, ServicioProveedorI servicioProveedor,
            ServicioTipoProducto servicioTipoProducto, ServicioMarca servicioMarca,
            ServicioTipoVentana servicioTipoVentana, ServicioTablas servicioTablas, ServicioClienteI servicioCliente,
            ServicioCarritoCoti servicioCarritoCoti) {
        this.servicioProducto = servicioProducto;
        this.servicioProveedor = servicioProveedor;
        this.servicioTipoProducto = servicioTipoProducto;
        this.servicioMarca = servicioMarca;
        this.servicioTipoVentana = servicioTipoVentana;
        this.servicioTablas = servicioTablas;
        this.clienteService = servicioCliente;
        this.carritoService = servicioCarritoCoti;
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
            throw new ProveedorNoExistente();
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
        // manejamos excepciones primero
        Proveedor proveedor = servicioProveedor.buscarPorId(idProveedor);
        if (proveedor == null) {
            throw new ProveedorNoExistente("El proveedor con ID " + idProveedor + " no existe.");
        }

        Producto producto = servicioProducto.obtenerPorId(idProducto);
        if (producto == null) {
            throw new NoHayProductoExistente("El producto con ID " + idProducto + " no existe.");
        }

        UsuarioProvDTO provDTO = new UsuarioProvDTO(idProveedor, proveedor.getRazonSocial(), proveedor.getLogoPath(),
                proveedor.getRubro());

        ModelMap modelMap = new ModelMap();
        modelMap.put("proveedor", provDTO);
        modelMap.put("producto", producto);

        return new ModelAndView("detalle-producto", modelMap);
    }

    // FLUJO 2: Explorar por productos semejantes
    // el detalle de producto sera como una comparacion
    // boton solicitar cotizacion
    // si el cliente no esta logueado solicitar q se loguee
    // si lo esta, se notifica N proveedores que tiene ese producto

    /*
     * @GetMapping("/ver-productos")
     * public ModelAndView verCatalogoPorProductosGenericos(){
     * ModelMap modelMap = new ModelMap();
     * List<ProductoGenericoDTO> productosGenericos =
     * servicioProducto.obtenerProductosGenericos();
     * modelMap.put("productosGenericos", productosGenericos);
     * return new ModelAndView("catalogo-producto-generico", modelMap);
     * 
     * }
     */

    @GetMapping("/ver-productos")
    public ModelAndView verCatalogo(HttpServletRequest request,
            @RequestParam(required = false) Long tipoProductoId,
            @RequestParam(required = false) Long tipoVentanaId,
            @RequestParam(required = false) Long anchoId,
            @RequestParam(required = false) Long altoId,
            @RequestParam(required = false) Long materialPerfilId,
            @RequestParam(required = false) Long colorId) {

        ModelMap modelMap = new ModelMap();

        UsuarioSesionDto sesionDto = (UsuarioSesionDto) request.getSession().getAttribute("usuarioLogueado");
        boolean estaLogueado = (sesionDto != null);

        modelMap.put("estaLogueado", estaLogueado);

        List<TipoProducto> tiposProductos = servicioTipoProducto.obtener();
        modelMap.put("tiposProducto", tiposProductos);

        List<Alto> alturas = servicioTablas.obtenerAltos();
        modelMap.put("alturas", alturas);

        List<Ancho> anchos = servicioTablas.obtenerAnchos();
        modelMap.put("anchos", anchos);

        List<MaterialDePerfil> materialesPerfil = servicioTablas.obtenerMateriales();
        modelMap.put("materialesPerfil", materialesPerfil);

        List<Color> colores = servicioTablas.obtenerColores();
        modelMap.put("colores", colores);

        List<Producto> productos = servicioProducto.filtrarProductos(tipoProductoId, tipoVentanaId, anchoId, altoId,
                materialPerfilId,
                colorId);

        // bbdd

        if (productos == null || productos.isEmpty()) {
            modelMap.put("mensaje", "No hay productos disponibles. Queres hacer una licitacion?");
            return new ModelAndView("catalogo", modelMap);
        }

        List<ProductoDTO> productoDTOs = convertirProductosADtos(productos);

        modelMap.put("productos", productoDTOs);

        return new ModelAndView("catalogo", modelMap);
        // luego preguntar si un cliente inicio sesion
    }

    @GetMapping("/tiposVentana/{tipoProductoId}")
    @ResponseBody
    public List<TipoVentana> obtenerTiposDeVentana(@PathVariable Long tipoProductoId) {
        try {
            return servicioTipoVentana.obtenerPorIdTipoProducto(tipoProductoId);

        } catch (NoHayProductoExistente e) {
            return Collections.emptyList();
        }

    }

    @GetMapping("/detalle-producto/{productoId}")
    public ModelAndView verDetalleProducto(@PathVariable Long productoId) {
        ModelMap modelMap = new ModelMap();

        Producto producto = servicioProducto.obtenerPorId(productoId);
        if (producto == null) {
            throw new NoHayProductoExistente();
            // modelMap.put("mensaje", "El producto no existe.");
            // return new ModelAndView("detalle-producto", modelMap);
        }

        modelMap.put("producto", producto);

        return new ModelAndView("detalle-producto", modelMap);
    }

    @GetMapping("/cotizar/{idProducto}")
    @ResponseBody
    public ResponseEntity<?> agregarProductoCarritoCoti(@PathVariable Long idProducto, HttpServletRequest request) {

        UsuarioSesionDto sesionDto = (UsuarioSesionDto) request.getSession().getAttribute("usuarioLogueado");

        if (sesionDto == null || sesionDto.getRol().equalsIgnoreCase("PROVEEDOR")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensaje", "Debe iniciar sesión como cliente para cotizar un producto."));
        }

        Cliente cliente = clienteService.buscarPorId(sesionDto.getId());

        if (cliente == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensaje", "Hubo un error de sesion. No se lo encontro registrado"));

        Producto producto = servicioProducto.obtenerPorId(idProducto);
        if (producto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensaje", "Producto no encontrado."));
        }

        CarritoCotizacion carrito = carritoService.obtenerOCrearCarrito(cliente);
        carrito.agregarItem(producto, 1);
        carritoService.guardar(carrito);

        // Retornar el contenido del carrito json
        List<Map<String, Object>> items = carrito.getItems().stream()
                .map(item -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", item.getId());
                    map.put("nombre", item.getProducto().getNombre());
                    map.put("imagenUrl", item.getProducto().getImagenUrl());
                    map.put("cantidad", item.getCantidad());
                    map.put("precio", item.getProducto().getPrecio());
                    return map;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of("items", items));
    }

    @DeleteMapping("/cotizar/eliminar/{idItem}")
    @ResponseBody
    public ResponseEntity<?> eliminarItemCarrito(@PathVariable Long idItem, HttpServletRequest request) {
        UsuarioSesionDto sesionDto = (UsuarioSesionDto) request.getSession().getAttribute("usuarioLogueado");

        if (sesionDto == null || sesionDto.getRol().equalsIgnoreCase("PROVEEDOR")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensaje", "Debe iniciar sesión como cliente para modificar la cotización."));
        }

        Cliente cliente = clienteService.buscarPorId(sesionDto.getId());
        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensaje", "Hubo un error de sesión."));
        }

        CarritoCotizacion carrito = carritoService.obtenerOCrearCarrito(cliente);

        boolean eliminado = carrito.eliminarItemPorId(idItem);
        carritoService.guardar(carrito);

        if (!eliminado) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensaje", "Item no encontrado en el carrito."));
        }

        // Devolver carrito actualizado
        List<Map<String, Object>> items = carrito.getItems().stream()
                .map(item -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", item.getId());
                    map.put("nombre", item.getProducto().getNombre());
                    map.put("imagenUrl", item.getProducto().getImagenUrl());
                    map.put("cantidad", item.getCantidad());
                    map.put("precio", item.getProducto().getPrecio());
                    return map;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of(
                "mensaje", "Producto eliminado del carrito.",
                "items", items));
    }

    @PutMapping("/cotizar/cambiar-cantidad/{idItem}")
    @ResponseBody
    public ResponseEntity<?> cambiarCantidadItem(
            @PathVariable Long idItem,
            @RequestBody Map<String, Integer> body,
            HttpServletRequest request) {

        UsuarioSesionDto sesionDto = (UsuarioSesionDto) request.getSession().getAttribute("usuarioLogueado");

        if (sesionDto == null || sesionDto.getRol().equalsIgnoreCase("PROVEEDOR")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensaje", "Debe iniciar sesión como cliente para modificar la cotización."));
        }

        Cliente cliente = clienteService.buscarPorId(sesionDto.getId());
        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensaje", "Hubo un error de sesión."));
        }

        Integer nuevaCantidad = body.get("cantidad");
        if (nuevaCantidad == null || nuevaCantidad < 1) {
            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", "Cantidad inválida."));
        }

        CarritoCotizacion carrito = carritoService.obtenerOCrearCarrito(cliente);
        boolean actualizado = carrito.actualizarCantidad(idItem, nuevaCantidad);
        carritoService.guardar(carrito);

        if (!actualizado) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensaje", "Item no encontrado en el carrito."));
        }

        List<Map<String, Object>> items = carrito.getItems().stream()
                .map(item -> {
                    Map<String, Object> map = new HashMap<>();

                    map.put("id", item.getId());
                    map.put("nombre", item.getProducto().getNombre());
                    map.put("imagenUrl", item.getProducto().getImagenUrl());
                    map.put("cantidad", item.getCantidad());
                    map.put("precio", item.getProducto().getPrecio());
                    return map;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of(
                "mensaje", "Cantidad actualizada correctamente.",
                "items", items));
    }

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

            TipoProducto tipoProducto = uno.getTipoProducto();

            Marca marca = uno.getMarca();

            Proveedor proveedor = uno.getProveedor();

            productoDTO.setTipoProducto(tipoProducto.getNombre());

            productoDTO.setMarca(marca.getNombre());

            productoDTO.setPrecio(uno.getPrecio());

            productoDTO.setNombreProveedor(proveedor.getRazonSocial());

            productoDTOs.add(productoDTO);
        }
        return productoDTOs;
    }

}
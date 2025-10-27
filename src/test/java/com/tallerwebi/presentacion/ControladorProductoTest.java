package com.tallerwebi.presentacion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

import com.tallerwebi.dominio.entidades.Marca;
import com.tallerwebi.dominio.entidades.Presentacion;
import com.tallerwebi.dominio.entidades.Producto;
import com.tallerwebi.dominio.entidades.Proveedor;
import com.tallerwebi.dominio.entidades.TipoProducto;
import com.tallerwebi.dominio.excepcion.NoHayProductoExistente;
import com.tallerwebi.dominio.excepcion.ProductoExistente;
import com.tallerwebi.dominio.servicios.ServicioMarca;
import com.tallerwebi.dominio.servicios.ServicioPresentacion;
import com.tallerwebi.dominio.servicios.ServicioProducto;
import com.tallerwebi.dominio.servicios.ServicioProveedorI;
import com.tallerwebi.dominio.servicios.ServicioTipoProducto;
import com.tallerwebi.presentacion.dto.UsuarioSesionDto;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.doNothing;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class ControladorProductoTest {
    private ControladorProducto controladorProductos;
    private ServicioProducto servicioProducto;
    private ServicioTipoProducto servicioTipoProducto;
    private ServicioMarca servicioMarca;
    private ServicioPresentacion servicioPresentacion;
    private ServicioProveedorI servicioProveedor;
    private HttpServletRequest requestMock;
    private HttpSession sessionMock;

    @BeforeEach
    public void init() {
        this.servicioProducto = mock(ServicioProducto.class);
        this.servicioTipoProducto = mock(ServicioTipoProducto.class);
        this.servicioMarca = mock(ServicioMarca.class);
        this.servicioPresentacion = mock(ServicioPresentacion.class);
        this.servicioProveedor = mock(ServicioProveedorI.class);
        requestMock = mock(HttpServletRequest.class);
        sessionMock = mock(HttpSession.class);
        this.controladorProductos = new ControladorProducto(this.servicioProducto, this.servicioTipoProducto,
                this.servicioMarca, this.servicioPresentacion, servicioProveedor);
    }

    @Test
    public void consultarProductosSinHaberAgregadoNingunoTengoMensajeNoHayProductos() {
        UsuarioSesionDto uSesionDto = new UsuarioSesionDto();
        uSesionDto.setId(3L);
        uSesionDto.setRol("Proveedor");
        uSesionDto.setUsername("proveedor@empresa.com");
        // tengo q simular sesion con usuario logueado
        when(sessionMock.getAttribute("usuarioLogueado")).thenReturn(uSesionDto);
        when(requestMock.getSession()).thenReturn(sessionMock);

        Proveedor proveedorMock = new Proveedor();
        proveedorMock.setId(3L); // el ID que luego se usa para buscar productos
        when(servicioProveedor.obtenerPorIdUsuario(3L)).thenReturn(proveedorMock);
        // preparacion
        doThrow(NoHayProductoExistente.class).when(servicioProducto).obtener();

        // ejecución
        ModelAndView modelAndView = controladorProductos.mostrarProductos(requestMock);

        // verificación
        assertThat(modelAndView.getViewName(), equalToIgnoringCase("producto-listado"));
        List<Producto> productosDto = (List<Producto>) modelAndView.getModel().get("productos");
        assertThat(productosDto.size(), equalTo(0));
        assertThat(modelAndView.getModel().get("error").toString(), equalToIgnoringCase("No hay Productos"));
    }

    @Test
    public void dadoQueExistenProductosCuandoLasConsultoSeMuestran3Productos() {

        // Peticion de tipo GET
        UsuarioSesionDto uSesionDto = new UsuarioSesionDto();
        uSesionDto.setId(3L);
        uSesionDto.setRol("Proveedor");
        uSesionDto.setUsername("proveedor@empresa.com");
        // tengo q simular sesion con usuario logueado
        when(sessionMock.getAttribute("usuarioLogueado")).thenReturn(uSesionDto);
        when(requestMock.getSession()).thenReturn(sessionMock);

        Proveedor proveedorMock = new Proveedor();
        proveedorMock.setId(3L); // el ID que luego se usa para buscar productos
        when(servicioProveedor.obtenerPorIdUsuario(3L)).thenReturn(proveedorMock);

        List<Producto> productosDto = new ArrayList<>();
        productosDto.add(new Producto());
        productosDto.add(new Producto());
        productosDto.add(new Producto());
        when(servicioProducto.buscarPorProveedorId(3L)).thenReturn(productosDto);
        // preparacion

        // ejecucion
        ModelAndView modelAndView = controladorProductos.mostrarProductos(requestMock);

        // verificacion
        assertThat(modelAndView.getViewName(), equalToIgnoringCase("producto-listado"));
        List<Producto> productosDtoObtenidos = (List<Producto>) modelAndView.getModel().get("productos");
        assertThat(productosDtoObtenidos.size(), equalTo(3));
        assertThat(modelAndView.getModel().get("exito").toString(), equalToIgnoringCase("Hay productos."));
    }
    // Peticion POST,PUT, DELETE
    // Validar que la vista sea la correcta

    @Test
    public void cuandoGuardoProductoValidoRedirigeAListadoConExito() throws ProductoExistente {
        when(requestMock.getSession()).thenReturn(sessionMock);
        // creo un usuario proveedor
        UsuarioSesionDto uSesionDto = new UsuarioSesionDto();
        uSesionDto.setRol("Proveedor");
        uSesionDto.setUsername("proveedor@empresa.com");
        // tengo q simular sesion con usuario logueado
        when(sessionMock.getAttribute("usuarioLogueado")).thenReturn(uSesionDto);

        Producto producto = new Producto();
        TipoProducto tipoProducto = new TipoProducto();
        tipoProducto.setId(1L);

        Presentacion presentacion = new Presentacion();
        presentacion.setId(1L);

        Marca marca = new Marca();
        marca.setId(1L);

        Proveedor proveedor = new Proveedor();
        proveedor.setId(3L);

        producto.setNombre("Ventana doble vidrio");
        producto.setPrecio(1200.50);
        producto.setProveedor(proveedor);
        producto.setStock(10);
        producto.setPresentacion(presentacion);
        producto.setTipoProducto(tipoProducto);
        producto.setMarca(marca);

        // Configuramos el mock
        doNothing().when(servicioProducto).crearProducto(any(Producto.class));

        ModelAndView modelAndView = controladorProductos.crearProducto(producto, null, requestMock);

        assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:listado"));
    }

}

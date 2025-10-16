package com.tallerwebi.presentacion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

import com.tallerwebi.dominio.entidades.Marca;
import com.tallerwebi.dominio.entidades.Presentacion;
import com.tallerwebi.dominio.entidades.Producto;
import com.tallerwebi.dominio.entidades.Proveedor;
import com.tallerwebi.dominio.entidades.TipoProducto;
import com.tallerwebi.dominio.entidades.Usuario;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
    private  ServicioTipoProducto servicioTipoProducto;
    private  ServicioMarca servicioMarca;    
    private  ServicioPresentacion servicioPresentacion;
    private  ServicioProveedorI servicioProveedorI;
    private HttpServletRequest requestMock;
    private HttpSession sessionMock;
    private Usuario usuarioMock;
    

    @BeforeEach
    public void init(){
        requestMock = mock(HttpServletRequest.class);
        sessionMock= mock(HttpSession.class);
        this.servicioProducto =  mock(ServicioProducto.class);
        this.servicioTipoProducto = mock(ServicioTipoProducto.class);
        this.servicioMarca = mock(ServicioMarca.class);
        this.servicioPresentacion = mock(ServicioPresentacion.class);
        this.servicioProveedorI = mock(ServicioProveedorI.class);
        this.controladorProductos = new ControladorProducto(this.servicioProducto, this.servicioTipoProducto, this.servicioMarca, this.servicioPresentacion,servicioProveedorI);
        usuarioMock = mock(Usuario.class);
		when(usuarioMock.getEmail()).thenReturn("pedro.gomez@email.com");
        when(usuarioMock.getId()).thenReturn(3L);
        when(usuarioMock.getNombre()).thenReturn("Pedro Gomez");
        when(usuarioMock.getRol()).thenReturn("PROVEEDOR");
    }

    @Test
    public void consultarProductosSinHaberAgregadoNingunoTengoMensajeNoHayProductos() {
        //preparacion
        doThrow(NoHayProductoExistente.class).when(servicioProducto).obtener();
        
        //ejecución
        ModelAndView modelAndView = controladorProductos.mostrarProductos();
        
        //verificación
        assertThat(modelAndView.getViewName(), equalToIgnoringCase("producto-listado"));
        List<Producto> productosDto =  (List<Producto>)modelAndView.getModel().get("productos");
        assertThat(productosDto.size(), equalTo(0));
        assertThat(modelAndView.getModel().get("error").toString(), equalToIgnoringCase("No hay Productos"));
    }

    @Test
    public void dadoQueExistenProductosCuandoLasConsultoSeMuestran3Productos() {

        // Peticion de tipo GET
        List<Producto> productosDto = new ArrayList<>();
         productosDto.add(new Producto());
        productosDto.add(new Producto());
        productosDto.add(new Producto());        
        when(servicioProducto.obtener()).thenReturn(productosDto);
        // preparacion

        // ejecucion
        ModelAndView modelAndView = controladorProductos.mostrarProductos();

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

        //creo un usuario proveedor
        UsuarioSesionDto uSesionDto = new UsuarioSesionDto();
        uSesionDto.setId(3L);
        uSesionDto.setRol("PROVEEDOR");
        uSesionDto.setUsername("pedro.gomez@email.com");

        //tengo q simular sesion con usuario logueado
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

    when(servicioProveedorI.obtenerPorIdUsuario(uSesionDto.getId()))
        .thenReturn(proveedor);

    // Configuramos el mock
    doNothing().when(servicioProducto).crearProducto(any(Producto.class));

    ModelAndView modelAndView = controladorProductos.crearProducto(producto, null, requestMock);

    verify(servicioProveedorI, times(1)).obtenerPorIdUsuario(uSesionDto.getId());
    
    // Verificamos que se haya llamado a crearProducto una vez (se puede verificar el objeto final)
    verify(servicioProducto, times(1)).crearProducto(any(Producto.class));

    assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:listado"));
}


}

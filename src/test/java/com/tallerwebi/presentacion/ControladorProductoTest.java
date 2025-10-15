package com.tallerwebi.presentacion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

import com.tallerwebi.dominio.entidades.Marca;
import com.tallerwebi.dominio.entidades.Presentacion;
import com.tallerwebi.dominio.entidades.Producto;
import com.tallerwebi.dominio.entidades.TipoProducto;
import com.tallerwebi.dominio.excepcion.NoHayProductoExistente;
import com.tallerwebi.dominio.excepcion.ProductoExistente;
import com.tallerwebi.dominio.servicios.ServicioMarca;
import com.tallerwebi.dominio.servicios.ServicioPresentacion;
import com.tallerwebi.dominio.servicios.ServicioProducto;
import com.tallerwebi.dominio.servicios.ServicioTipoProducto;

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

public class ControladorProductoTest {
    private ControladorProducto controladorProductos;
    private ServicioProducto servicioProducto;
    private  ServicioTipoProducto servicioTipoProducto;
    private  ServicioMarca servicioMarca;    
    private  ServicioPresentacion servicioPresentacion;

    @BeforeEach
    public void init(){
        this.servicioProducto =  mock(ServicioProducto.class);
        this.servicioTipoProducto = mock(ServicioTipoProducto.class);
        this.servicioMarca = mock(ServicioMarca.class);
        this.servicioPresentacion = mock(ServicioPresentacion.class);
        this.controladorProductos = new ControladorProducto(this.servicioProducto, this.servicioTipoProducto, this.servicioMarca, this.servicioPresentacion);
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
    Producto producto = new Producto();
    TipoProducto tipoProducto = new TipoProducto();
    tipoProducto.setId(1L);
    
    Presentacion presentacion = new Presentacion();
    presentacion.setId(1L);
    

    Marca marca = new Marca();
    marca.setId(1L);

    producto.setNombre("Ventana doble vidrio");
    producto.setPrecio(1200.50);    
    producto.setProveedorId(1);
    producto.setStock(10);
    producto.setPresentacion(presentacion);
    producto.setTipoProducto(tipoProducto);
    producto.setMarca(marca);

    // Configuramos el mock
    doNothing().when(servicioProducto).crearProducto(any(Producto.class));

    ModelAndView modelAndView = controladorProductos.crearProducto(producto, null);

    assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:listado"));
}


}

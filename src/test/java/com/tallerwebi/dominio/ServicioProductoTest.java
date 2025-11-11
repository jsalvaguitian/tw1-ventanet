package com.tallerwebi.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.tallerwebi.dominio.entidades.Marca;
import com.tallerwebi.dominio.entidades.Presentacion;
import com.tallerwebi.dominio.entidades.Producto;
import com.tallerwebi.dominio.entidades.Proveedor;
import com.tallerwebi.dominio.entidades.TipoProducto;
import com.tallerwebi.dominio.excepcion.NoHayProductoExistente;
import com.tallerwebi.dominio.excepcion.ProductoExistente;
import com.tallerwebi.dominio.servicios.ServicioProductoImpl;
import com.tallerwebi.infraestructura.RepositorioProductoImpl;

public class ServicioProductoTest {
    private RepositorioProductoImpl productoRepositoryMock;
    private ServicioProductoImpl servicioProducto;
    @BeforeEach
    void setUp() {
        productoRepositoryMock = mock(RepositorioProductoImpl.class);
        servicioProducto = new ServicioProductoImpl(productoRepositoryMock);        
    }

    @Test
    void cuandoProductoNoExisteDebeGuardarlo() throws ProductoExistente {
        // Arrange
        Producto producto = crearProducto("Vidrio templado", 1L, 2L);

        when(productoRepositoryMock.obtenerPorNombreMarcaYProveedor(
                producto.getNombre(),
                producto.getMarca().getId(),
                producto.getProveedor().getId()))
            .thenReturn(null); // No existe aún

        servicioProducto.crearProducto(producto);

        // Assert
        ArgumentCaptor<Producto> captor = ArgumentCaptor.forClass(Producto.class);
        verify(productoRepositoryMock, times(1)).guardar(captor.capture());

        Producto productoGuardado = captor.getValue();
        assertEquals("Vidrio templado", productoGuardado.getNombre());
        assertEquals(1L, productoGuardado.getMarca().getId());
        assertEquals(2L, productoGuardado.getProveedor().getId());
    }

    @Test
    void cuandoProductoYaExisteDebeLanzarExcepcion() {
        // Arrange
        Producto producto = crearProducto("Ventana doble", 1L, 3L);
        when(productoRepositoryMock.obtenerPorNombreMarcaYProveedor(
                anyString(), anyLong(), anyLong()))
            .thenReturn(producto);
        
        assertThrows(ProductoExistente.class, () -> servicioProducto.crearProducto(producto));

        verify(productoRepositoryMock, never()).guardar(any(Producto.class));
    }

    private Producto crearProducto(String nombre, Long idMarca, Long idProveedor) {
        Producto producto = new Producto();
        producto.setNombre(nombre);

        Marca marca = new Marca();
        marca.setId(idMarca);
        producto.setMarca(marca);

        Proveedor proveedor = new Proveedor();
        proveedor.setId(idProveedor);
        producto.setProveedor(proveedor);

        producto.setPrecio(5000);
        producto.setStock(3);

        TipoProducto tipoProducto = new TipoProducto();
        tipoProducto.setId(1L);
        producto.setTipoProducto(tipoProducto);

        Presentacion presentacion = new Presentacion();
        presentacion.setId(1L); 
        producto.setPresentacion(presentacion);

        return producto;
    }
        
    // ========================================================
    // ============ TESTS DEL MÉTODO eliminar =================
    // ========================================================

    @Test
    void cuandoProductoExisteDebeEliminarlo() {
        Producto producto = crearProducto("Mosquitero", 2L, 5L);
        when(productoRepositoryMock.obtener(10L)).thenReturn(producto);

        servicioProducto.eliminar(10L);

        verify(productoRepositoryMock, times(1)).obtener(10L);
        verify(productoRepositoryMock, times(1)).eliminar(10L);
    }

    @Test
    void cuandoProductoNoExisteDebeLanzarNoHayProductoExistente() {
        when(productoRepositoryMock.obtener(99L)).thenReturn(null);

        assertThrows(NoHayProductoExistente.class, () -> servicioProducto.eliminar(99L));

        verify(productoRepositoryMock, times(1)).obtener(99L);
        verify(productoRepositoryMock, never()).eliminar(anyLong());
    }

    // ========================================================
    // ============ TESTS DEL MÉTODO actualizar ================
    @Test
    void cuandoSeActualizaProductoDebeLlamarAlRepositorio() {
        Producto producto = crearProducto("Puerta corrediza", 1L, 3L);

        when(productoRepositoryMock.actualizar(producto)).thenReturn(true);;

        servicioProducto.actualizar(producto);

        verify(productoRepositoryMock, times(1)).actualizar(producto);
    }

    @Test
    void cuandoRepositorioLanzaExcepcionAlActualizarDebePropagarse() {
        Producto producto = crearProducto("Persiana", 1L, 3L);

        doThrow(new RuntimeException("Error de base de datos"))
            .when(productoRepositoryMock).actualizar(producto);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> servicioProducto.actualizar(producto));

        assertEquals("Error de base de datos", ex.getMessage());
        verify(productoRepositoryMock, times(1)).actualizar(producto);
    }
}

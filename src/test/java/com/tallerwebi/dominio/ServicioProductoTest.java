package com.tallerwebi.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.tallerwebi.dominio.entidades.Marca;
import com.tallerwebi.dominio.entidades.Producto;
import com.tallerwebi.dominio.entidades.Proveedor;
import com.tallerwebi.dominio.servicios.ServicioProductoImpl;
import com.tallerwebi.infraestructura.RepositorioProductoImpl;

public class ServicioProductoTest {
    private RepositorioProductoImpl productoRepository;
    private ServicioProductoImpl servicioProducto;
    private Producto producto;

    @BeforeEach
    void setUp() {
        Marca marca = new Marca();
        marca.setId(1L);

        Proveedor proveedor = new Proveedor();
        proveedor.setId(3L);

        producto = new Producto();
        producto.setId(10L);
        producto.setNombre("Mouse Gamer");
        producto.setMarca(marca);
        producto.setProveedor(proveedor);
    }

    // @Test
    // void deberiaRetornarListaDeProductos() {
    //     when(productoRepository.obtener()).thenReturn(List.of(producto));

    //     List<Producto> resultado = servicioProducto.obtener();

    //     assertEquals(1, resultado.size());
    //     assertEquals("Mouse Gamer", resultado.get(0).getNombre());
    //     verify(productoRepository).obtener();
    // }
    
}

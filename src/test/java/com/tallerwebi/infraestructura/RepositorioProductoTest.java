package com.tallerwebi.infraestructura;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.tallerwebi.dominio.entidades.Producto;

public class RepositorioProductoTest {

     @Mock
    private SessionFactory sessionFactoryMock;

    @Mock
    private Session sessionMock;

    @Mock
    private Criteria criteriaMock;

    @InjectMocks
    private RepositorioProductoImpl repositorioProducto;


    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        when(sessionFactoryMock.getCurrentSession()).thenReturn(sessionMock);
    }

    private Producto crearProducto() {
        Producto p = new Producto();
        p.setId(1L);
        p.setNombre("Ventana de aluminio");
        return p;
    }

    @Test
    void cuandoSeCreaUnProductoDebeLlamarASave() {
        Producto producto = crearProducto();

        repositorioProducto.guardar(producto);

        verify(sessionMock, times(1)).save(producto);
    }

    @Test
    void cuandoSeActualizaUnProductoDebeLlamarAUpdate() {
        Producto producto = crearProducto();

        repositorioProducto.actualizar(producto);

        verify(sessionMock, times(1)).update(producto);
    }
    
}

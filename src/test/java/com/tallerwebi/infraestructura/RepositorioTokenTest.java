package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.entidades.ResetearPasswordToken;
import com.tallerwebi.dominio.repositorios_interfaces.RepositorioTokenRecuperarPasswordImpl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Criteria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class RepositorioTokenTest {

    @Mock
    private SessionFactory sessionFactory;
    @Mock
    private Session session;
    @Mock
    private Criteria criteria;

    private RepositorioTokenRecuperarPasswordImpl repositorio;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        when(sessionFactory.getCurrentSession()).thenReturn(session);
        repositorio = new RepositorioTokenRecuperarPasswordImpl(sessionFactory);
    }

    @Test
    void deberiaGuardarToken() {
        ResetearPasswordToken token = new ResetearPasswordToken();
        token.setToken("ola");

        // Simulamos hibernate
        when(session.createCriteria(ResetearPasswordToken.class)).thenReturn(criteria);
        when(criteria.add(any())).thenReturn(criteria);
        when(criteria.uniqueResult()).thenReturn(token);

        repositorio.guardar(token);
        ResetearPasswordToken tokenEsperado = repositorio.buscarPorToken(token.getToken());
        verify(session).save(token);
        assertThat(tokenEsperado.getToken(), notNullValue());
        assertThat(tokenEsperado.getToken(), is("ola"));
    }

    @Test
    void deberiaEliminarToken() {
        ResetearPasswordToken token = new ResetearPasswordToken();
        repositorio.eliminar(token);
        verify(session).delete(token);
    }
}

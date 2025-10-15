package com.tallerwebi.infraestructura;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.tallerwebi.dominio.entidades.Proveedor;
import com.tallerwebi.dominio.entidades.UsuarioAuth;
import com.tallerwebi.dominio.utils.PasswordUtil;

/*
 * Aclaracion: Testeando RepositorioFake Sin Hibernate ORM. Luego se borrara
 */
/* 
public class RepositorioUsuarioAuthImplTest {

    RepositorioUsuarioAuthImpl repo;

    @BeforeEach
    public void init(){
        repo = new RepositorioUsuarioAuthImpl();
    }

    @Test
    public void queSePuedaInicializarLaBaseDeDatosFakeUsuariosConDosUsuarios(){
        Integer cantidadDeUsuariosEsperada = 2;

        Integer cantidadDeUsuariosObtenida = repo.getUsuariosDB().size();

        assertThat(cantidadDeUsuariosObtenida,equalTo(cantidadDeUsuariosEsperada));
    }

    @Test
    public void dadoTenerUnUsuarioCuandoLoGuardoObtengoUnResultadoVerdadero(){
        Proveedor unProveedor = new Proveedor("proveedor@empresa.mail.com", PasswordUtil.hashear("Proveedor1!"));
        Integer cantidadEsperada = 3; //se inicializo con 2 usuarios + 1 este;
        
        Boolean resultadoGuardado = repo.guardar(unProveedor);
        Integer cantidadObtenida = repo.getUsuariosDB().size();
        
        assertTrue(resultadoGuardado);
        assertThat(cantidadObtenida, equalTo(cantidadEsperada));
    }

    @Test
    public void dadoUnEmailCuandoBuscoUnUsuarioPorEmailObtengoElUsuarioBuscado(){
        String emailABuscar = "jesi@gmail.com";

        UsuarioAuth encontrado = repo.buscarPorMail(emailABuscar);

        assertThat(encontrado.getEmail(), equalTo(emailABuscar));
    }



}*/

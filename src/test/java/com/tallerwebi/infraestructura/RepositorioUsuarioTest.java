package com.tallerwebi.infraestructura;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

//import javax.mail.Session;
import javax.transaction.Transactional;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import com.tallerwebi.dominio.entidades.Proveedor;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.repositorios_interfaces.RepositorioUsuario;
import com.tallerwebi.infraestructura.config.CloudinaryTestConfig;
import com.tallerwebi.infraestructura.config.HibernateTestConfig;
import com.tallerwebi.infraestructura.config.SpringWebTestConfig;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = {SpringWebTestConfig.class , HibernateTestConfig.class, CloudinaryTestConfig.class})
public class RepositorioUsuarioTest {

    @Autowired
    private SessionFactory sessionFactory;
    private RepositorioUsuario repositorioUsuario;
    @BeforeEach
    public void init(){
        repositorioUsuario = new RepositorioUsuarioImpl(sessionFactory);  
    }

    @Test
    @Transactional
    @Rollback
    public void dadoQueExisteUnProveedorEnLaBDCuandoLoObtengoPorMail(){
        Proveedor proveedor = new Proveedor();
        proveedor.setEmail("proveedor@gmail.com");
        proveedor.setPassword("Proveedor1!");
        proveedor.setCuit("20304050607");

        this.sessionFactory.getCurrentSession().save(proveedor);

        Usuario usuarioBuscado = repositorioUsuario.buscarPorMail("proveedor@gmail.com");

        assertThat(usuarioBuscado, is(notNullValue()));
        assertThat(usuarioBuscado.getEmail(), is(equalTo("proveedor@gmail.com")));
    }

    

}

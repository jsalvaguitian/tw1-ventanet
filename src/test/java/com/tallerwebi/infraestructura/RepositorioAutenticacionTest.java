package com.tallerwebi.infraestructura;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.repositorios_interfaces.RepositorioUsuario;
import com.tallerwebi.infraestructura.config.HibernateTestConfig;
import com.tallerwebi.infraestructura.config.SpringWebTestConfig;
/*

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = {SpringWebTestConfig.class , HibernateTestConfig.class })
public class RepositorioAutenticacionTest {

    @Autowired
    private SessionFactory sessionFactory;
    private RepositorioUsuario repositorioUsuario;  

    @BeforeEach
    public void init(){
        this.repositorioUsuario = new RepositorioUsuarioImpl(sessionFactory);
    }


    @Test
    @Transactional
    @Rollback
    public void dadoQueExisteUsuarioProveedorEnLaBDCuandoLoObtengoPorMailOCuitMeDevuelveEseUsuario(){

        Usuario usuarioProveedor = new Usuario();
        usuarioProveedor.setEmail("proveedor@outlook.com");
        usuarioProveedor.setPassword("Elbarto1!");
        usuarioProveedor.setRolUsuario(Rol.PROVEEDOR);
        usuarioProveedor.setCuit("20304050607");

        this.sessionFactory.getCurrentSession().save(usuarioProveedor);

        Usuario usuarioBuscado = this.repositorioUsuario.buscarPorEmailOCuit("proveedor@outlook.com", "20304050607");

        assert(usuarioBuscado != null);
        assert(usuarioBuscado.getEmail().equals("proveedor@outlook.com"));

    }

  
}
*/
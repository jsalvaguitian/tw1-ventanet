package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.tallerwebi.dominio.entidades.Proveedor;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.enums.EstadoUsuario;
import com.tallerwebi.dominio.excepcion.CuentaNoActivaException;
import com.tallerwebi.dominio.excepcion.CuentaPendienteException;
import com.tallerwebi.dominio.excepcion.CuentaRechazadaException;
import com.tallerwebi.dominio.excepcion.UsuarioInexistenteException;
import com.tallerwebi.dominio.repositorios_interfaces.RepositorioProveedor;
import com.tallerwebi.dominio.repositorios_interfaces.RepositorioUsuario;
import com.tallerwebi.dominio.servicios.ServicioEmail;
import com.tallerwebi.dominio.servicios.ServicioUsuario;
import com.tallerwebi.dominio.servicios.ServicioUsuarioImpl;
import com.tallerwebi.dominio.utils.PasswordUtil;

public class ServicioUserTest {

    private RepositorioUsuario repositorioUsuario;
    private RepositorioProveedor repositorioProveedor;
    private ServicioEmail servicioEmail;
    private ServicioUsuario servicioUsuario;

    @BeforeEach
    public void init(){
        //crear mock de los repositorios y servicios que no probaremos sus funcionalidades
        repositorioUsuario = mock(RepositorioUsuario.class);
        repositorioProveedor = mock(RepositorioProveedor.class);
        servicioEmail = mock(ServicioEmail.class);

        //inyectar los mockitos en el servicio a probar
        servicioUsuario = new ServicioUsuarioImpl(repositorioUsuario, repositorioProveedor, servicioEmail);
    }

    @Test
    public void dadoUnUsuarioExistentesConCredencialesValidasCuandoInicieSesionEntoncesDevuelveElUsuario() throws UsuarioInexistenteException, CuentaNoActivaException, CuentaPendienteException, CuentaRechazadaException{
        //preparacion
        String correo = "jesi@email.com";
        Usuario proveedor = new Proveedor();
        proveedor.setEmail(correo);
        proveedor.setPassword(PasswordUtil.hashear("Password123!"));    
        proveedor.setEstado(EstadoUsuario.ACTIVO);

        //mockear el comportamiento del repositorio
        when(repositorioUsuario.buscarPorMail(correo)).thenReturn(proveedor);

        Usuario resultado = servicioUsuario.iniciarSesion(correo, "Password123!");

        assertThat(resultado.getEmail(), equalTo(correo));
    }
    

 
    @Test
    public void dadoUnProveedorConUnEmailNoRegistradoQueSuAutenticacionFalle(){

        String emailIngresadoNoRegistrado = "belen@gmail.com";
        String contraseniaIngresada = "Goku123!";

        assertThrows(UsuarioInexistenteException.class, ()-> servicioUsuario.iniciarSesion(emailIngresadoNoRegistrado, contraseniaIngresada));

    }

}

package com.tallerwebi.dominio;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import com.tallerwebi.dominio.excepcion.UsuarioInexistenteException;
/* 
public class ServicioUsuarioTest {

    private ServicioUsuarioAuthImpl servicioUsuarioImpl;
    

    @BeforeEach
    public void init(){
   
    
        servicioUsuarioImpl = new ServicioUsuarioAuthImpl();
    }

    @Test
    public void dadoUnProveedorConCredencialesValidasQueSeAutenticaExitosamente() throws UsuarioInexistenteException{
        String emailIngresado = "jesi@gmail.com";
        String contraseniaIngresada = "Elbarto1!";
        String rolEsperado = "Proveedor";

        UsuarioAuth usuarioAuth = servicioUsuarioImpl.autenticar(emailIngresado, contraseniaIngresada);
        String rolObtenido = usuarioAuth.getRol();

        assertThat(rolObtenido, equalTo(rolEsperado));

    }


    @Test
    public void dadoUnProveedorConUnEmailNoRegistradoQueSuAutenticacionFalle(){

        String emailIngresadoNoRegistrado = "belen@gmail.com";
        String contraseniaIngresada = "Goku123!";

        assertThrows(UsuarioInexistenteException.class, ()-> servicioUsuarioImpl.autenticar(emailIngresadoNoRegistrado, contraseniaIngresada));

    }

}
*/
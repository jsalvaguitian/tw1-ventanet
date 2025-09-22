package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

import com.tallerwebi.dominio.entidades.UsuarioAuth;
import com.tallerwebi.dominio.excepcion.UsuarioInexistenteException;
import com.tallerwebi.dominio.servicios.ServicioUsuarioI;
import com.tallerwebi.presentacion.dto.UsuarioDto;

import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;


public class ControladorLoginUsuarioTest {


    private ControladorAuthLogin controladorLogin;
    private ServicioUsuarioI servicioUsuarioI;

    @BeforeEach
    public void init(){
        controladorLogin = new ControladorAuthLogin(servicioUsuarioI);

    }

    @Test
    public void queSeMuestrePorPantallaElLoginParaQueSePuedaLoguearUsuario(){
        //preparacion 
        String vistaEsperada = "login-usuario";
        
        //ejecucion
        ModelAndView modelAndView = controladorLogin.mostrarLogin();
        String vistaObtenida = modelAndView.getViewName();


        //validacion
        assertThat(vistaObtenida, equalTo(vistaEsperada));
        assertTrue(modelAndView.getModel().containsKey("usuarioDto"));
    }

    

    @Test
    public void dadoUnMailVacioQueElLoginFalle(){
        
    }

    @Test
    public void dadoUnMailConFormatoInvalidoQueElLoginFalle(){

    }

    @Test
    public void dadoUnaContraseniaVaciaQueElLoginFalle(){}

    //simular acciones de servicios
    @Test
    public void dadoUnProveedorConCredencialesValidasDebeDirigirseA() throws UsuarioInexistenteException{


      
    }

    @Test
    public void dadoUnMailInexisteCuandoElUsuarioSeLogueeEntoncesElLoginFalle(){

    }

    

    @Test
    public void dadoUnMailRegistradoYUnContraseneaErroneaQueElLoginFalle(){

    }

    



}

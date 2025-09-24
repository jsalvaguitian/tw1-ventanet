package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.tallerwebi.dominio.utils.PasswordUtil;

public class PasswordUtilTest {

    @Test
    public void dadoUnaContraseniaCuandoSeHasheaMeDevuelvaOtraCosaQueNoSeaElPWD(){
        //preparacion
        String contraseniaAHashear = "soyUnPassword";

        //ejecucion
        String hashing = PasswordUtil.hashear(contraseniaAHashear);

        //validacion
        assertNotEquals(contraseniaAHashear, hashing);
    }

    @Test
    public void dadoUnaContraseniaIncorrectaCuandoLaVerificoMeRetorneFalso(){
        String pwdElPosta = "Pikachu";
        String pwdIncorrecto = "holamundo";

        String hashValido = PasswordUtil.hashear(pwdElPosta);

        assertFalse(PasswordUtil.verificar(pwdIncorrecto, hashValido));
    }

}

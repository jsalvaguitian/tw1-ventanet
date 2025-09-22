package com.tallerwebi.dominio.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtil {
    private static final BCryptPasswordEncoder codificador = new BCryptPasswordEncoder();

    public static boolean verificar(String contraseniaIngresadoLogin, String contraseniaHasheadaBD){
        return codificador.matches(contraseniaIngresadoLogin, contraseniaHasheadaBD);
    }
}

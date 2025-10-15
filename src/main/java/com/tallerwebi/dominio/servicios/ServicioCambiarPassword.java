package com.tallerwebi.dominio.servicios;

public interface ServicioCambiarPassword {

    boolean cambiarPassword(String nuevaPassword, String token);

}

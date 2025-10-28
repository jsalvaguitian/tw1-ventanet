package com.tallerwebi.dominio.excepcion;

public class CuentaRechazadaException extends Exception {
    public CuentaRechazadaException() {
        super("Tu solicitud fue rechazada. Contacta al soporte.");
    }

}

package com.tallerwebi.dominio.excepcion;

public class NoSePudoObtenerDolarException extends RuntimeException {
    public NoSePudoObtenerDolarException(String mensaje) {
        super(mensaje);
    }
}
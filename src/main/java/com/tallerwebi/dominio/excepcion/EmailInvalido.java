package com.tallerwebi.dominio.excepcion;

public class EmailInvalido extends Exception {
    public EmailInvalido(String mensaje) {
        super(mensaje);
    }
}

package com.tallerwebi.dominio.excepcion;

public class CuentaNoActivaException extends Exception {
    public CuentaNoActivaException() {
        super("Debes verificar tu correo electrónico para activar tu cuenta.");
    }

}

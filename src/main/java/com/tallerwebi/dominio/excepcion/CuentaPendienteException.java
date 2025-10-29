package com.tallerwebi.dominio.excepcion;

public class CuentaPendienteException extends Exception {
    public CuentaPendienteException() {
        super("Tu cuenta está pendiente de aprobación por el administrador.");
    }

}

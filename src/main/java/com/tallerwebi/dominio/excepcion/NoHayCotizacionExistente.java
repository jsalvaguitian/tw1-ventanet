package com.tallerwebi.dominio.excepcion;

public class NoHayCotizacionExistente extends Exception{
    public NoHayCotizacionExistente(String mensaje) {
            super(mensaje);
        }
}

package com.tallerwebi.dominio.excepcion;

public class NoHayCotizacionExistente extends Exception{
        public NoHayCotizacionExistente() {
        super("No se encontró la cotización especificada.");
    }
    public NoHayCotizacionExistente(String message) {
        super(message);
    }
}
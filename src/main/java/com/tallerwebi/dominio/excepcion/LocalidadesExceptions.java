package com.tallerwebi.dominio.excepcion;

public class LocalidadesExceptions {

    public static class NoHayLocalidadExistente extends RuntimeException {
        public NoHayLocalidadExistente(String mensaje) {
            super(mensaje);
        }
    }
    
}
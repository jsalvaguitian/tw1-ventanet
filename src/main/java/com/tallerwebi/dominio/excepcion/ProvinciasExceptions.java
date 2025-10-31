package com.tallerwebi.dominio.excepcion;

public class ProvinciasExceptions {

    public static class NoHayProvinciaExistente extends RuntimeException {
        public NoHayProvinciaExistente(String mensaje) {
            super(mensaje);
        }
    }
    
}
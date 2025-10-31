package com.tallerwebi.dominio.excepcion;

public class PartidosExceptions {

    public static class NoHayPartidoExistente extends RuntimeException {
        public NoHayPartidoExistente(String mensaje) {
            super(mensaje);
        }
    }
    
}
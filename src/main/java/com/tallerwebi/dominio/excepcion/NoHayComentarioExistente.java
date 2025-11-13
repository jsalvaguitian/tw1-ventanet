package com.tallerwebi.dominio.excepcion;

public class NoHayComentarioExistente extends RuntimeException {
 public NoHayComentarioExistente(String mensaje) {
        super(mensaje);
    }

}

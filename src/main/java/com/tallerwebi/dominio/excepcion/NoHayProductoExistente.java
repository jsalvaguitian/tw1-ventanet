package com.tallerwebi.dominio.excepcion;

public class NoHayProductoExistente extends RuntimeException {
    public NoHayProductoExistente() {
        super("No se encontró el producto especificado.");
    }
    public NoHayProductoExistente(String message) {
        super(message);
    }
}

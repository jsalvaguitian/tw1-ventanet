package com.tallerwebi.dominio.excepcion;

public class ProveedorNoExistente extends RuntimeException {
    public ProveedorNoExistente() {
        super("No se encontró el proveedor especificado.");
    }
    public ProveedorNoExistente(String message) {
        super(message);
    }
}

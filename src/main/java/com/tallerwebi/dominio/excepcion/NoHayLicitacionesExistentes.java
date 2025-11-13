package com.tallerwebi.dominio.excepcion;

public class NoHayLicitacionesExistentes extends Exception{
    public NoHayLicitacionesExistentes() {
        super("No se encontraron licitaciones existentes.");
    }
}

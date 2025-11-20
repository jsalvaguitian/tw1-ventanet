package com.tallerwebi.dominio.enums;
//Anotaciones para recordar los estados posibles de un usuario

public enum EstadoUsuario {
    ACTIVO("Activo"),
    NO_ACTIVO("Inactivo"),
    PENDIENTE("Pendiente"),
    RECHAZADO("Rechazado");

    private final String descripcion;

    EstadoUsuario(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
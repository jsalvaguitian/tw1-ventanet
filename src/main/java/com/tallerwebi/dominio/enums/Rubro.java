package com.tallerwebi.dominio.enums;

public enum Rubro {
    VIDRIOS_ABERTURAS("Vidrios y aberturas", "bi bi-window"),
    PISOS_REVESTIMIENTOS("Pisos y revestimientos", "bi bi-grid-3x3-gap"),
    MATERIALES_OBRA("Materiales básicos de obra", "bi bi-bricks"),
    CARPINTERIA("Carpintería", "bi bi-hammer"),
    ELECTRICIDAD_ILUMINACION("Electricidad e iluminación", "bi bi-lightbulb"),
    PLOMERIA_GAS("Plomería y gas", "bi bi-droplet"),
    PINTURAS_ACABADOS("Pinturas y acabados", "bi bi-palette"),
    SEGURIDAD_HIGIENE("Seguridad e higiene", "bi bi-shield-check"),
    HERRAMIENTAS_MAQUINARIAS("Herramientas y maquinarias", "bi bi-tools"),
    HIERROS_ESTRUCTURAS("Hierros y estructuras", "bi bi-building");

    private final String descripcion;
    private final String icono;

    Rubro(String descripcion, String icono) {
        this.descripcion = descripcion;
        this.icono = icono;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getIcono() {
        return icono;
    }
}

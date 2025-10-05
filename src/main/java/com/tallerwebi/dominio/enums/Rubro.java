package com.tallerwebi.dominio.enums;

public enum Rubro {
    MATERIALES_OBRA("Materiales básicos de obra"),
    HIERROS_ESTRUCTURAS("Hierros y estructuras"),
    CARPINTERIA("Carpintería"),
    PISOS_REVESTIMIENTOS("Pisos y revestimientos"),
    SANITARIOS_GRIFERIA("Sanitarios y grifería"),
    ELECTRICIDAD_ILUMINACION("Electricidad e iluminación"),
    PLOMERIA_GAS("Plomería y gas"),
    HERRAMIENTAS_MAQUINARIAS("Herramientas y maquinarias"),
    SEGURIDAD_HIGIENE("Seguridad e higiene"),
    VIDRIOS_ABERTURAS("Vidrios y aberturas"),
    PINTURAS_ACABADOS("Pinturas y acabados");

    private final String descripcion;

    Rubro(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

}

package com.tallerwebi.dominio.entidades;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.tallerwebi.dominio.enums.UnidadMedida;

@Embeddable
public class Dimensiones {
    private Double ancho;
    private Double alto;
    private Double largo;
    private Double espesor;

    @Enumerated(EnumType.STRING)
    private UnidadMedida unidadMedida;

    public Dimensiones() {
    }


    
    public Dimensiones(Double ancho, Double alto, Double largo, Double espesor, UnidadMedida unidadMedida) {
        this.ancho = ancho;
        this.alto = alto;
        this.largo = largo;
        this.espesor = espesor;
        this.unidadMedida = unidadMedida;
    }



    public Double getAncho() {
        return ancho;
    }

    public void setAncho(Double ancho) {
        this.ancho = ancho;
    }

    public Double getAlto() {
        return alto;
    }

    public void setAlto(Double alto) {
        this.alto = alto;
    }

    public Double getLargo() {
        return largo;
    }

    public void setLargo(Double largo) {
        this.largo = largo;
    }

    public Double getEspesor() {
        return espesor;
    }

    public void setEspesor(Double espesor) {
        this.espesor = espesor;
    }

    public UnidadMedida getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(UnidadMedida unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    

}

package com.tallerwebi.dominio.entidades;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import com.tallerwebi.dominio.enums.EstadoUsuario;

@Entity
@DiscriminatorValue("CLIENTE")
public class Cliente extends Usuario {

    // @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval =
    // true)
    // private List<Cotizacion> cotizaciones = new ArrayList<>();
    /*
     * @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval =
     * true)
     * private List<Cotizacion> cotizaciones = new ArrayList<>();
     */

    public Cliente() {
        super();
        this.estado = EstadoUsuario.NO_ACTIVO;
    }

    // public List<Cotizacion> getCotizaciones() {
    // return cotizaciones;
    // }

    // public void setCotizaciones(List<Cotizacion> cotizaciones) {
    // this.cotizaciones = cotizaciones;
    // }
    /*
     * public List<Cotizacion> getCotizaciones() {
     * return cotizaciones;
     * }
     * 
     * public void setCotizaciones(List<Cotizacion> cotizaciones) {
     * this.cotizaciones = cotizaciones;
     * }
     */

}
package com.tallerwebi.dominio.entidades;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.tallerwebi.dominio.enums.Rubro;

@Entity
public class TipoProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String nombre;

    @ManyToOne
    private Rubro rubro;

    public TipoProducto() {
    }

    public TipoProducto(String nombre, Rubro rubro) {
        this.nombre = nombre;
        this.rubro = rubro;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public Rubro getRubro() {
        return rubro;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setRubro(Rubro rubro) {
        this.rubro = rubro;
    }

    
}

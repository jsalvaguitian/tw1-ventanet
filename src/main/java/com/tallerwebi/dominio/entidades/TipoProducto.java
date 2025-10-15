package com.tallerwebi.dominio.entidades;

import javax.persistence.*;

@Entity
public class TipoProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    // 🔹 Constructor vacío obligatorio para JPA
    public TipoProducto() {
    }

    public TipoProducto(String nombre) {
        this.nombre = nombre;
    }

    // 🔹 Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}

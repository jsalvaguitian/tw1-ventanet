package com.tallerwebi.dominio.entidades;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import com.tallerwebi.dominio.enums.TipoMedioPago;

@Entity
public class MedioDePago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    private Integer cantidad_cuotas;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TipoMedioPago tipo;

    private String imagen;

    @ManyToMany(mappedBy = "mediosDePago")
    private List<Proveedor> proveedores = new ArrayList<>();

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

    public Integer getCantidad_cuotas() {
        return cantidad_cuotas;
    }

    public void setCantidad_cuotas(Integer cantidad_cuotas) {
        this.cantidad_cuotas = cantidad_cuotas;
    }

    public TipoMedioPago getTipo() {
        return tipo;
    }

    public void setTipo(TipoMedioPago tipo) {
        this.tipo = tipo;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        MedioDePago that = (MedioDePago) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}

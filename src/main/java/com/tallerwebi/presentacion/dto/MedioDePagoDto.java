package com.tallerwebi.presentacion.dto;

import com.tallerwebi.dominio.enums.TipoMedioPago;

public class MedioDePagoDto {
    private Long id;
    private String nombre;
    private String imagen;
    private TipoMedioPago tipo;
    
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
    public String getImagen() {
        return imagen;
    }
    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
    public TipoMedioPago getTipo() {
        return tipo;
    }
    public void setTipo(TipoMedioPago tipo) {
        this.tipo = tipo;
    }

    
}

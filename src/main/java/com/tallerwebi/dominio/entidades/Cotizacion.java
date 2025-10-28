package com.tallerwebi.dominio.entidades;

import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.tallerwebi.dominio.enums.Color;

@Entity
public class Cotizacion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long clienteId;
    private String nombre;
    private Integer cantidad;
    private String ubicacion;
    private Double alto;
    private Double ancho;
    private String materialPerfil;
    private String tipoVidrio;
    @Enumerated(EnumType.STRING)
    private Color color;
    private LocalDate fechaCotizacion;


   
    public void setId(Long id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public void setAlto(Double alto) {
        this.alto = alto;
    }

    public void setAncho(Double ancho) {
        this.ancho = ancho;
    }

    public void setMaterialPerfil(String materialPerfil) {
        this.materialPerfil = materialPerfil;
    }

    public void setTipoVidrio(String tipoVidrio) {
        this.tipoVidrio = tipoVidrio;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public Double getAlto() {
        return alto;
    }

    public Double getAncho() {
        return ancho;
    }

    public String getMaterialPerfil() {
        return materialPerfil;
    }

    public String getTipoVidrio() {
        return tipoVidrio;
    }

    public Color getColor() {
        return color;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public LocalDate getFechaCotizacion() {
        return fechaCotizacion;
    }

    public void setFechaCotizacion(LocalDate fechaCotizacion) {
        this.fechaCotizacion = fechaCotizacion;
    }

    public Long getClienteId() {
        return clienteId;
    }
    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }
}

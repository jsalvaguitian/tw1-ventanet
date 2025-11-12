package com.tallerwebi.dominio.entidades;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class ProductoCustom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;    
    private double precio;
    private String descripcion;    
    private String imgCloudinaryID;    
    @ManyToOne(optional = false)    
    private Proveedor proveedor;      
    private Double ancho;
    private Double alto;
    private Double largo;
    private Double espesor;      
    private String color;
    private String modelo;    
    private String tipoMaterial;
    private Boolean aceptaEnvio;
    private Integer cantidad; 
    @OneToOne(mappedBy = "productoCustom", fetch = FetchType.LAZY)
    @JsonIgnore
    private Licitacion licitacion;   

    public Licitacion getLicitacion() {
        return licitacion;
    }

    public void setLicitacion(Licitacion licitacion) {
        this.licitacion = licitacion;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getTipoMaterial() {
        return tipoMaterial;
    }
    
    public Boolean getAceptaEnvio() {
        return aceptaEnvio;
    }

    public void setAceptaEnvio(Boolean aceptaEnvio) {
        this.aceptaEnvio = aceptaEnvio;
    }

    public void setId(long id) {
        this.id = id;
    }    

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }          

    
    public long getId() {
        return id;
    }    

    public double getPrecio() {
        return precio;
    }

    public String getDescripcion() {
        return descripcion;
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getImgCloudinaryID() {
        return imgCloudinaryID;
    }

    public void setImgCloudinaryID(String imgCloudinaryID) {
        this.imgCloudinaryID = imgCloudinaryID;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
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

    public void setTipoMaterial(String tipoMaterial) {
        this.tipoMaterial = tipoMaterial;
    }

}

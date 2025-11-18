package com.tallerwebi.presentacion.dto;

import javax.persistence.ManyToOne;

public class ProductoCustomDto {
    private long id;    
    private double precio;
    private String descripcion;    
    private String imgCloudinaryID;    
    @ManyToOne(optional = false)    
    private UsuarioProvDTO proveedor;      
    private Double ancho;
    private Double alto;
    private Double largo;
    private Double espesor;      
    private String color;
    private String modelo;    
    private String tipoMaterial;
    private Boolean aceptaEnvio;
    private Integer cantidad;
    private String rubro;
    
    public String getRubro() {
        return rubro;
    }
    public void setRubro(String rubro) {
        this.rubro = rubro;
    }
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }    
    public double getPrecio() {
        return precio;
    }
    public void setPrecio(double precio) {
        this.precio = precio;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public String getImgCloudinaryID() {
        return imgCloudinaryID;
    }
    public void setImgCloudinaryID(String imgCloudinaryID) {
        this.imgCloudinaryID = imgCloudinaryID;
    }
    
    public UsuarioProvDTO getProveedor() {
        return proveedor;
    }
    public void setProveedor(UsuarioProvDTO proveedor) {
        this.proveedor = proveedor;
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
    public String getColor() {
        return color;
    }
    public void setColor(String color) {
        this.color = color;
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
    public void setTipoMaterial(String tipoMaterial) {
        this.tipoMaterial = tipoMaterial;
    }
    public Boolean getAceptaEnvio() {
        return aceptaEnvio;
    }
    public void setAceptaEnvio(Boolean aceptaEnvio) {
        this.aceptaEnvio = aceptaEnvio;
    }
    public Integer getCantidad() {
        return cantidad;
    }
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    } 
}

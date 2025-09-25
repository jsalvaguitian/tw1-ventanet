package com.tallerwebi.dominio.entidades;

public class Producto {
    private long id;
    private String nombre;
    private double precio;
    private String descripcion;
    private String imagenUrl;
    private int stock;
    private Integer tipoProductoId;
    private Integer marcaId;
    private Integer proveedorId;
    private Integer presentacionId;

    public void setId(long id) {
        this.id = id;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public void setPrecio(double precio) {
        this.precio = precio;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }
    public void setStock(int stock) {
        this.stock = stock;
    }
    public void setTipoProductoId(Integer tipoProductoId) {
        this.tipoProductoId = tipoProductoId;
    }
    public void setMarcaId(Integer marcaId) {
        this.marcaId = marcaId;
    }
    public void setProveedorId(Integer proveedorId) {
        this.proveedorId = proveedorId;
    }
    public void setPresentacionId(Integer presentacionId) {
        this.presentacionId = presentacionId;
    }
    public long getId() {
        return id;
    }
    public String getNombre() {
        return nombre;
    }
    public double getPrecio() {
        return precio;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public String getImagenUrl() {
        return imagenUrl;
    }
    public int getStock() {
        return stock;
    }
    public Integer getTipoProductoId() {
        return tipoProductoId;
    }
    public Integer getMarcaId() {
        return marcaId;
    }
    public Integer getProveedorId() {
        return proveedorId;
    }
    public Integer getPresentacionId() {
        return presentacionId;
    }

    
}

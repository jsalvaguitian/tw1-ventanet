package com.tallerwebi.presentacion.dto;

public class ProductoDTO {
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
    
    public ProductoDTO() {
    }   
    
    public ProductoDTO(String nombre, double precio, String descripcion, String imagenUrl, int stock,
            int tipo_producto_id, int marca_id, int proveedor_id, int presentacion_id) {
        this.nombre = nombre;
        this.precio = precio;
        this.descripcion = descripcion;
        this.imagenUrl = imagenUrl;
        this.stock = stock;
        this.tipoProductoId = tipo_producto_id;
        this.marcaId = marca_id;
        this.proveedorId = proveedor_id;
        this.presentacionId = presentacion_id;
    }

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
    public void setTipoProductoId(Integer tipo_producto_id) {
        this.tipoProductoId = tipo_producto_id;
    }
    public void setMarcaId(Integer marca_id) {
        this.marcaId = marca_id;
    }
    public void setProveedorId(Integer proveedor_id) {
        this.proveedorId = proveedor_id;
    }
    public void setPresentacionId(Integer presentacion_id) {
        this.presentacionId = presentacion_id;
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
    public Integer getStock() {
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

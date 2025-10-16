package com.tallerwebi.presentacion.dto;

public class ProductoDTO {
    private long id;
    private String nombre;
    private double precio;
    private String descripcion;
    private String imagenUrl;
    private int stock;
    private Long tipoProductoId;
    private Long marcaId;
    private Integer proveedorId;
    private Integer presentacionId;
    private String tipoProducto;
    private String marca;
    
    public ProductoDTO() {
    }   
    
    public ProductoDTO(String nombre, double precio, String descripcion, String imagenUrl, int stock,
            int tipo_producto_id, int marca_id, int proveedor_id, int presentacion_id) {
        this.nombre = nombre;
        this.precio = precio;
        this.descripcion = descripcion;
        this.imagenUrl = imagenUrl;
        this.stock = stock;
        this.tipoProductoId = (long) tipo_producto_id;
        this.marcaId = (long) marca_id;
        this.proveedorId = proveedor_id;
        this.presentacionId = presentacion_id;

    }

    public ProductoDTO(String nombre, String descripcion, String imagenUrl, Long tipoProductoId, Long marcaId) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.imagenUrl = imagenUrl;
        this.tipoProductoId = tipoProductoId;
        this.marcaId = marcaId;
    }

    
    public String getTipoProducto() {
        return tipoProducto;
    }

    public void setTipoProducto(String tipoProducto) {
        this.tipoProducto = tipoProducto;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
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
    public void setTipoProductoId(Long tipo_producto_id) {
        this.tipoProductoId = tipo_producto_id;
    }
    public void setMarcaId(Long marca_id) {
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
    public Long getTipoProductoId() {
        return tipoProductoId;
    }
    public Long getMarcaId() {
        return marcaId;
    }
    public Integer getProveedorId() {
        return proveedorId;
    }
    public Integer getPresentacionId() {
        return presentacionId;
    }
    
}

package com.tallerwebi.presentacion.dto;

public class ProductoDetalleDTO {
    private Integer idProveedor;
    private String marca;
    private String descripcion;
    private String imgUrl;
    private double precio;
    
    public ProductoDetalleDTO(Integer idProveedor, String marca, String descripcion, String imgUrl, double precio) {
        this.idProveedor = idProveedor;
        this.marca = marca;
        this.descripcion = descripcion;
        this.imgUrl = imgUrl;
        this.precio = precio;
    }

    public Integer getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    

    

}

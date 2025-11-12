package com.tallerwebi.presentacion.dto;

public class ProductoImportDTO {

    private String nombre;
    private double precio;
    private String descripcion;
    private String nombreImagen;
    private Integer stock;
    
    private String tipoProducto;
    private String marca;
    private String presentacion;
    private String subtipoProducto;
    private String ancho;
    private String alto;
    private String material;
    private String tipoVidrio;
    private String color;
    private String urlImagen;
    private String imgCloudinaryId;

    private String error;
    private String advertencia;
    private Boolean valido;

    // private String nombreProveedor;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public ProductoImportDTO() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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

    public String getNombreImagen() {
        return nombreImagen;
    }

    public void setNombreImagen(String nombreImagen) {
        this.nombreImagen = nombreImagen;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
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

    public String getPresentacion() {
        return presentacion;
    }

    public void setPresentacion(String presentacion) {
        this.presentacion = presentacion;
    }

    public String getSubtipoProducto() {
        return subtipoProducto;
    }

    public void setSubtipoProducto(String subTipoProducto) {
        this.subtipoProducto = subTipoProducto;
    }

    public String getAncho() {
        return ancho;
    }

    public void setAncho(String ancho) {
        this.ancho = ancho;
    }

    public String getAlto() {
        return alto;
    }

    public void setAlto(String alto) {
        this.alto = alto;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getTipoVidrio() {
        return tipoVidrio;
    }

    public void setTipoVidrio(String tipoVidrio) {
        this.tipoVidrio = tipoVidrio;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Boolean getValido() {
        return valido;
    }

    public void setValido(Boolean valido) {
        this.valido = valido;
    }

    public void setUrlImagen(String urlImagen) {
        this.urlImagen = urlImagen;
    }

    public void setImgCloudinaryID(String imgCloudinaryID) {
        this.imgCloudinaryId = imgCloudinaryID;
    }

    public String getUrlImagen() {
        return urlImagen;
    }

    public String getImgCloudinaryId() {
        return imgCloudinaryId;
    }

    public String getAdvertencia() {
        return advertencia;
    }

    public void setAdvertencia(String advertencia) {
        this.advertencia = advertencia;
    }

    


}

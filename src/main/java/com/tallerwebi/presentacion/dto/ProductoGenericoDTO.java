package com.tallerwebi.presentacion.dto;

import java.util.List;

public class ProductoGenericoDTO {
    private String nombreGenerico;
    private String tipoProducto;
    private String tipoVentana;
    private String materialPerfil;
    private String tipoVidrio;
    private String tipoMaterial;
    private String dimensiones;
    private String color;
    private Long cantidadProveedores;

    private List<ProductoDetalleDTO> productosProveedor;

    public ProductoGenericoDTO(String tipoProducto, String tipoVentana, String materialPerfil, String tipoVidrio, String dimensiones, Long cantidadProveedores) {
        this.tipoProducto = tipoProducto;
        this.tipoVentana = tipoVentana;
        this.materialPerfil = materialPerfil;
        this.tipoVidrio = tipoVidrio;
        this.dimensiones = dimensiones;
        this.cantidadProveedores = cantidadProveedores;

        //genero nombre generico a mostrar
        this.nombreGenerico = tipoProducto + " " +
        (tipoVentana != null ? tipoVentana + " ": "") +
        (materialPerfil != null ? materialPerfil + " ": "") +
        (tipoVidrio != null ? tipoVidrio + " ": "");
    }

    public ProductoGenericoDTO() {
    }

    public String getNombreGenerico() {
        return nombreGenerico;
    }

    public void setNombreGenerico(String nombreGenerico) {
        this.nombreGenerico = nombreGenerico;
    }

    public String getTipoProducto() {
        return tipoProducto;
    }

    public void setTipoProducto(String tipoProducto) {
        this.tipoProducto = tipoProducto;
    }

    public String getTipoVentana() {
        return tipoVentana;
    }

    public void setTipoVentana(String tipoVentana) {
        this.tipoVentana = tipoVentana;
    }

    public String getMaterialPerfil() {
        return materialPerfil;
    }

    public void setMaterialPerfil(String materialPerfil) {
        this.materialPerfil = materialPerfil;
    }

    public String getTipoVidrio() {
        return tipoVidrio;
    }

    public void setTipoVidrio(String tipoVidrio) {
        this.tipoVidrio = tipoVidrio;
    }

    public String getTipoMaterial() {
        return tipoMaterial;
    }

    public void setTipoMaterial(String tipoMaterial) {
        this.tipoMaterial = tipoMaterial;
    }

    public String getDimensiones() {
        return dimensiones;
    }

    public void setDimensiones(String dimensiones) {
        this.dimensiones = dimensiones;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Long getCantidadProveedores() {
        return cantidadProveedores;
    }

    public void setCantidadProveedores(Long cantidadProveedores) {
        this.cantidadProveedores = cantidadProveedores;
    }

    public List<ProductoDetalleDTO> getProductosProveedor() {
        return productosProveedor;
    }

    public void setProductosProveedor(List<ProductoDetalleDTO> productosProveedor) {
        this.productosProveedor = productosProveedor;
    }

    

    

    


    



    



}

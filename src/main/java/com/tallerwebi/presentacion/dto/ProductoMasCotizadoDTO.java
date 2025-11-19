package com.tallerwebi.presentacion.dto;

public class ProductoMasCotizadoDTO {
    private Long productoId;
    private String nombre;
    private String imagenUrl;
    private Long cantidadTotal;
    
    public ProductoMasCotizadoDTO (Long productoId, String nombre, String imagenUrl, Long cantidadTotal) {
        this.productoId = productoId;
        this.nombre = nombre;
        this.imagenUrl = imagenUrl;
        this.cantidadTotal = cantidadTotal;
    }
    
    public Long getProductoId() {
        return productoId;
    }

    public String getNombre() {
        return nombre;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public Long getCantidadTotal() {
        return cantidadTotal;
    }
}
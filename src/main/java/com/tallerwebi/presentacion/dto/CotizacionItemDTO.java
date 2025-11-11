package com.tallerwebi.presentacion.dto;

public class CotizacionItemDTO {
    private int cantidad;    
    private Long idProducto;    
    

    public CotizacionItemDTO() {}

    public int getCantidad() {
        return cantidad;
    }
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }  
    
    
    public Long getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Long idProducto) {
        this.idProducto = idProducto;
    }
}

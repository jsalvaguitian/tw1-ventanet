package com.tallerwebi.presentacion.dto;

import java.time.LocalDate;
import java.util.List;

public class LicitacionDto {
    private Long id;
    private ProductoCustomDto productoCustom;
    private Double montoTotal;
    private List<Long> proveedoresIds;
    private LocalDate fechaCreacion;
    private LocalDate fechaExpiracion;    
    private String adjuntoUrl;
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public ProductoCustomDto getProductoCustom() {
        return productoCustom;
    }
    public void setProductoCustom(ProductoCustomDto productoCustom) {
        this.productoCustom = productoCustom;
    }
    public Double getMontoTotal() {
        return montoTotal;
    }
    public void setMontoTotal(Double montoTotal) {
        this.montoTotal = montoTotal;
    }
    public List<Long> getProveedoresIds() {
        return proveedoresIds;
    }
    public void setProveedoresIds(List<Long> proveedoresIds) {
        this.proveedoresIds = proveedoresIds;
    }
    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }
    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    public LocalDate getFechaExpiracion() {
        return fechaExpiracion;
    }
    public void setFechaExpiracion(LocalDate fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }
}

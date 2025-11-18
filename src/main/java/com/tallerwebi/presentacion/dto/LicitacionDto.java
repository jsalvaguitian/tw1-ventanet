package com.tallerwebi.presentacion.dto;

import java.time.LocalDate;
import java.util.List;

import com.tallerwebi.dominio.enums.EstadoLicitacion;

public class LicitacionDto {
    private Long id;
    private ProductoCustomDto productoCustom;
    private Double montoTotal;
    private List<Long> proveedoresIds;
    private Long clienteId;
    private UsuarioSesionDto cliente;
    private UsuarioProvDTO proveedor;
    private EstadoLicitacion estado;
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

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public UsuarioProvDTO getProveedor() {
        return proveedor;
    }

    public void setProveedor(UsuarioProvDTO proveedor) {
        this.proveedor = proveedor;
    }

    public EstadoLicitacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoLicitacion estado) {
        this.estado = estado;
    }

    public String getAdjuntoUrl() {
        return adjuntoUrl;
    }

    public void setAdjuntoUrl(String adjuntoUrl) {
        this.adjuntoUrl = adjuntoUrl;
    }

    public UsuarioSesionDto getCliente() {
        return cliente;
    }

    public void setCliente(UsuarioSesionDto cliente) {
        this.cliente = cliente;
    }
}

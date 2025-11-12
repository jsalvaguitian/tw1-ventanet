package com.tallerwebi.dominio.entidades;

import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import com.tallerwebi.dominio.enums.EstadoLicitacion;

@Entity
public class Licitacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;    
    private Double montoTotal;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "proveedor_id", nullable = false)
    private Proveedor proveedor;
    private LocalDate fechaCreacion;
    private LocalDate fechaExpiracion;
    private String adjuntoUrl;
    @Enumerated(EnumType.STRING)
    private EstadoLicitacion estado;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productoCustom_id", nullable = false)
    private ProductoCustom productoCustom;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public ProductoCustom getProductoCustom() {
        return productoCustom;
    }
    public void setProductoCustom(ProductoCustom productoCustom) {
        this.productoCustom = productoCustom;
    }
    public Double getMontoTotal() {
        return montoTotal;
    }
    public void setMontoTotal(Double montoTotal) {
        this.montoTotal = montoTotal;
    }
    public Cliente getCliente() {
        return cliente;
    }
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
    public Proveedor getProveedor() {
        return proveedor;
    }
    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
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
    public String getAdjuntoUrl() {
        return adjuntoUrl;
    }
    public void setAdjuntoUrl(String adjuntoUrl) {
        this.adjuntoUrl = adjuntoUrl;
    }
    public EstadoLicitacion getEstado() {
        return estado;
    }
    public void setEstado(EstadoLicitacion estado) {
        this.estado = estado;
    }
}

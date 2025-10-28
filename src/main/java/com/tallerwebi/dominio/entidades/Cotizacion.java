package com.tallerwebi.dominio.entidades;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.tallerwebi.dominio.enums.EstadoCotizacion;

@Entity
public class Cotizacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDate fechaCreacion;
    @Column(name = "fecha_expiracion")
    private LocalDate fechaExpiracion;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "proveedor_id", nullable = false)
    private Proveedor proveedor;
    @OneToMany(mappedBy = "cotizacion", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CotizacionItem> items = new ArrayList<>();
    @Column(name = "monto_total", nullable = false)
    private Double montoTotal = 0.0;
    @Enumerated(EnumType.STRING)
    private EstadoCotizacion estado;

    public void eliminarItem(CotizacionItem item) {
        this.items.remove(item);
        item.setCotizacion(null);
        recalcularMontoTotal();
    }

    public void recalcularMontoTotal() {
        this.montoTotal = items.stream()
                .mapToDouble(item -> {
                    Object subtotal = item.getSubtotal();
                    if (subtotal == null) {
                        return 0.0;
                    }
                    if (subtotal instanceof Number) {
                        return ((Number) subtotal).doubleValue();
                    }
                    if (subtotal instanceof String) {
                        try {
                            return Double.parseDouble((String) subtotal);
                        } catch (NumberFormatException e) {
                            return 0.0;
                        }
                    }
                    return 0.0;
                })
                .sum(); 
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<CotizacionItem> getItems() {
        return items;
    }

    public void setItems(List<CotizacionItem> items) {
        this.items = items;
    }

    public Double getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(Double montoTotal) {
        this.montoTotal = montoTotal;
    }

    public EstadoCotizacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoCotizacion estado) {
        this.estado = estado;
    }
}

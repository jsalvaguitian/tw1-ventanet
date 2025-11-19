package com.tallerwebi.dominio.entidades;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;

@Entity
public class Comentario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mensaje", nullable = false, length = 2000)
    private String mensaje;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cotizacion_id")
    private Cotizacion cotizacion; // nullable si pertenece a una licitacion

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "licitacion_id")
    private Licitacion licitacion; // nullable si pertence a una cotizacion

    // Autor del mensaje será o el cliente o el proveedor de la cotización.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente; // null si lo escribió el proveedor

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proveedor_id")
    private Proveedor proveedor; // null si lo escribió el cliente

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    // Flags para estado leído por cada parte
    @Column(name = "leido_por_cliente", nullable = false)
    private boolean leidoPorCliente = false;

    @Column(name = "leido_por_proveedor", nullable = false)
    private boolean leidoPorProveedor = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Cotizacion getCotizacion() {
        return cotizacion;
    }

    public void setCotizacion(Cotizacion cotizacion) {
        this.cotizacion = cotizacion;
    }

    public Licitacion getLicitacion() {
        return licitacion;
    }

    public void setLicitacion(Licitacion licitacion) {
        this.licitacion = licitacion;
    }

    @javax.persistence.Transient
    public boolean esDeCotizacion() { return cotizacion != null; }
    @javax.persistence.Transient
    public boolean esDeLicitacion() { return licitacion != null; }

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

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public boolean isLeidoPorCliente() {
        return leidoPorCliente;
    }

    public void setLeidoPorCliente(boolean leidoPorCliente) {
        this.leidoPorCliente = leidoPorCliente;
    }

    public boolean isLeidoPorProveedor() {
        return leidoPorProveedor;
    }

    public void setLeidoPorProveedor(boolean leidoPorProveedor) {
        this.leidoPorProveedor = leidoPorProveedor;
    }

    @javax.persistence.Transient
    public String getFechaCreacionFormatted() {
        if (fechaCreacion == null) return "";
        return fechaCreacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
}

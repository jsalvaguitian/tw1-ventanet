package com.tallerwebi.dominio.entidades;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class PresupuestoItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "presupuesto_id", nullable = false)
    private Presupuesto presupuesto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tipo_producto_id", nullable = false)
    private TipoProducto tipoProducto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tipo_ventana_id", nullable = false)
    private TipoVentana tipoVentana;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ancho_id", nullable = false)
    private Ancho ancho;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "alto_id", nullable = false)
    private Alto alto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "material_id", nullable = false)
    private MaterialDePerfil material;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "color_id", nullable = false)
    private Color color;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public Presupuesto getPresupuesto() { return presupuesto; }
    public void setPresupuesto(Presupuesto presupuesto) { this.presupuesto = presupuesto; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public TipoProducto getTipoProducto() { return tipoProducto; }
    public void setTipoProducto(TipoProducto tipoProducto) { this.tipoProducto = tipoProducto; }

    public TipoVentana getTipoVentana() { return tipoVentana; }
    public void setTipoVentana(TipoVentana tipoVentana) { this.tipoVentana = tipoVentana; }

    public Ancho getAncho() { return ancho; }
    public void setAncho(Ancho ancho) { this.ancho = ancho; }

    public Alto getAlto() { return alto; }
    public void setAlto(Alto alto) { this.alto = alto; }

    public MaterialDePerfil getMaterial() { return material; }
    public void setMaterial(MaterialDePerfil material) { this.material = material; }

    public Color getColor() { return color; }
    public void setColor(Color color) { this.color = color; }
}

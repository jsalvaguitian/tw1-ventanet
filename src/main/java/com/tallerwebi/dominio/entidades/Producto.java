package com.tallerwebi.dominio.entidades;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 155)
    private String nombre;
    private double precio;
    private String descripcion;
    private String imagenUrl;
    private int stock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_producto_id", nullable = false)
    private TipoProducto tipoProducto;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "marca_id", nullable = false)
    private Marca marca;

    private Integer proveedorId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "presentacion_id", nullable = false)
    private Presentacion presentacion;

    

    @Embedded //es para agrupar las dimensiones para mas orden igualmente seran campos de Producto
    private Dimensiones dimensiones;

    private String modelo;

    @ManyToOne
    private TipoMaterial TipoMaterial;

    @OneToMany(mappedBy = "producto")
    private Set<ProductoProveedor> proveedores = new LinkedHashSet<>();


    private Boolean aceptaEnvio;

    public void setId(long id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
    public void setTipoProducto(TipoProducto tipoProducto) {
        this.tipoProducto = tipoProducto;
    }
    public void setMarca(Marca marca) {
        this.marca = marca;
    }

    public void setProveedorId(Integer proveedorId) {
        this.proveedorId = proveedorId;
    }
    public void setPresentacion(Presentacion presentacion) {
        this.presentacion = presentacion;
    }

    public long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public int getStock() {
        return stock;
    }
    public TipoProducto getTipoProducto() {
        return tipoProducto;
    }
    public Marca getMarca() {
        return marca;
    }

    public Integer getProveedorId() {
        return proveedorId;
    }
    public Presentacion getPresentacion() {
        return presentacion;
    }    
}

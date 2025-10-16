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
    private String cuitProveedor;

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

    @Embedded // es para agrupar las dimensiones para mas orden igualmente seran campos de
              // Producto
    private Dimensiones dimensiones;

    private String modelo;

    @ManyToOne
    private TipoMaterial TipoMaterial;

    private Boolean aceptaEnvio;

    public String getCuitProveedor() {
        return cuitProveedor;
    }

    public void setCuitProveedor(String cuitProveedor) {
        this.cuitProveedor = cuitProveedor;
    }

    public Dimensiones getDimensiones() {
        return dimensiones;
    }

    public void setDimensiones(Dimensiones dimensiones) {
        this.dimensiones = dimensiones;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public TipoMaterial getTipoMaterial() {
        return TipoMaterial;
    }

    public void setTipoMaterial(TipoMaterial tipoMaterial) {
        TipoMaterial = tipoMaterial;
    }

    public Boolean getAceptaEnvio() {
        return aceptaEnvio;
    }

    public void setAceptaEnvio(Boolean aceptaEnvio) {
        this.aceptaEnvio = aceptaEnvio;
    }

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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((nombre == null) ? 0 : nombre.hashCode());
        result = prime * result + ((cuitProveedor == null) ? 0 : cuitProveedor.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Producto other = (Producto) obj;
        if (nombre == null) {
            if (other.nombre != null)
                return false;
        } else if (!nombre.equals(other.nombre))
            return false;
        if (cuitProveedor == null) {
            if (other.cuitProveedor != null)
                return false;
        } else if (!cuitProveedor.equals(other.cuitProveedor))
            return false;
        return true;
    }


}

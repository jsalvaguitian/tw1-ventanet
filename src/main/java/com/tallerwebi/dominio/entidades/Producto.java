package com.tallerwebi.dominio.entidades;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String nombre;
    private double precio;
    private String descripcion;
    private String imagenUrl;
    private int stock;
    private Integer tipoProductoId;
    private Integer marcaId;
    private Integer proveedorId;
    private Integer presentacionId;

    @ManyToOne
    private TipoProducto tipoProducto;

    @Embedded //es para agrupar las dimensiones para mas orden igualmente seran campos de Producto
    private Dimensiones dimensiones;

    @ManyToOne
    private Marca marca;

    private String modelo;

    @ManyToOne
    private TipoMaterial TipoMaterial;

    @OneToMany(mappedBy = "producto")
    private Set<ProductoProveedor> proveedores = new LinkedHashSet<>();

    /* 
    private Boolean disponible;

        @OneToMany(mappedBy = "producto")
    private List<DetalleCotizacion> detalleCotizacion; 
    */

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

    public void setTipoProductoId(Integer tipoProductoId) {
        this.tipoProductoId = tipoProductoId;
    }

    public void setMarcaId(Integer marcaId) {
        this.marcaId = marcaId;
    }

    public void setProveedorId(Integer proveedorId) {
        this.proveedorId = proveedorId;
    }

    public void setPresentacionId(Integer presentacionId) {
        this.presentacionId = presentacionId;
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

    public Integer getTipoProductoId() {
        return tipoProductoId;
    }

    public Integer getMarcaId() {
        return marcaId;
    }

    public Integer getProveedorId() {
        return proveedorId;
    }

    public Integer getPresentacionId() {
        return presentacionId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((nombre == null) ? 0 : nombre.hashCode());
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
        return true;
    }

    

}

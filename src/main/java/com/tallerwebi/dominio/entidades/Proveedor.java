package com.tallerwebi.dominio.entidades;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import com.tallerwebi.dominio.enums.EstadoProveedor;
import com.tallerwebi.dominio.enums.Rubro;


@Entity
@DiscriminatorValue("PROVEEDOR")
public class Proveedor extends Usuario{
    
    private String razonSocial;        // Nombre legal de la empresa
    private String cuit;               // Identificador fiscal
    
    @Enumerated(EnumType.STRING)
    private Rubro rubro;              
    private String sitioWeb; 
    
    @Enumerated(EnumType.STRING)
    private EstadoProveedor estado;             // Activo, Inactivo, Suspendido

    private String documento;        // Ruta del documento legal del proveedor

    @OneToMany(mappedBy = "proveedor")
    private Set<ProductoProveedor> productos = new LinkedHashSet<>();

    private String ubicacion;
    private Double latitud;
    private Double longitud;


    public Proveedor() {
        super();
        this.estado = EstadoProveedor.PENDIENTE;
    }


    public String getRazonSocial() {
        return razonSocial;
    }


    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }


    public String getCuit() {
        return cuit;
    }


    public void setCuit(String cuit) {
        this.cuit = cuit;
    }


    public Rubro getRubro() {
        return rubro;
    }


    public void setRubro(Rubro rubro) {
        this.rubro = rubro;
    }


    public String getSitioWeb() {
        return sitioWeb;
    }


    public void setSitioWeb(String sitioWeb) {
        this.sitioWeb = sitioWeb;
    }


    public EstadoProveedor getEstado() {
        return estado;
    }


    public void setEstado(EstadoProveedor estado) {
        this.estado = estado;
    }


    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    


    public String getUbicacion() {
        return ubicacion;
    }


    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }


    public Double getLatitud() {
        return latitud;
    }


    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }


    public Double getLongitud() {
        return longitud;
    }


    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((cuit == null) ? 0 : cuit.hashCode());
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
        Proveedor other = (Proveedor) obj;
        if (cuit == null) {
            if (other.cuit != null)
                return false;
        } else if (!cuit.equals(other.cuit))
            return false;
        return true;
    }


    public Set<ProductoProveedor> getProductos() {
        return productos;
    }


    public void setProductos(Set<ProductoProveedor> productos) {
        this.productos = productos;
    }

    
        
    /* Crearse la clase Producto
     private List<Producto> productos;       // Productos ofrecidos por el proveedor */
    //private List<Cotizacion> cotizaciones;  // Cotizaciones realizadas por el proveedor


    /*public Proveedor(String email, String contrasenia){
        super(email,contrasenia);
        this.cotizaciones = new ArrayList<>();
    }*/

    //private Double dolarReferencia;    // Último valor de dólar usado para precios
    

    
}


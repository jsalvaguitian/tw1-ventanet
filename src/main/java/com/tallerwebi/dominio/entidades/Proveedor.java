package com.tallerwebi.dominio.entidades;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;

import javax.persistence.CascadeType;

import com.tallerwebi.dominio.enums.EstadoUsuario;
import com.tallerwebi.dominio.enums.Rubro;


@Entity
@DiscriminatorValue("PROVEEDOR")
public class Proveedor extends Usuario{
    
    private String razonSocial;        // Nombre legal de la empresa
    private String cuit;               // Identificador fiscal
    
    @Enumerated(EnumType.STRING)
    private Rubro rubro;              
    private String sitioWeb; 
    /*
    @Enumerated(EnumType.STRING)
    private EstadoUsuario estado;             // Activo, Inactivo, Suspendido
*/
    private String documento;        // Ruta del documento legal del proveedor

    // @OneToMany(mappedBy = "proveedor")
    // private List<Producto> productos = new ArrayList<Producto>();

    private String ubicacion;
    private Double latitud;
    private Double longitud;


    public Proveedor() {
        super();
        this.estado = EstadoUsuario.PENDIENTE;
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


    public EstadoUsuario getEstado() {
        return estado;
    }


    public void setEstado(EstadoUsuario estado) {
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


    // public List<Producto> getProductos() {
    //     return productos;
    // }


    // public void setProductos(List<Producto> productos) {
    //     this.productos = productos;
    // }

    
}


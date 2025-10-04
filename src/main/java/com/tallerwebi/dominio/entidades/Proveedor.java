package com.tallerwebi.dominio.entidades;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

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


        
    /* Crearse la clase Producto
     private List<Producto> productos;       // Productos ofrecidos por el proveedor */
    //private List<Cotizacion> cotizaciones;  // Cotizaciones realizadas por el proveedor


    /*public Proveedor(String email, String contrasenia){
        super(email,contrasenia);
        this.cotizaciones = new ArrayList<>();
    }*/

    //private Double dolarReferencia;    // Último valor de dólar usado para precios
    

    
}


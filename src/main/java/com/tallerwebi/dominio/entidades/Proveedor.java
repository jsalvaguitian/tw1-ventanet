package com.tallerwebi.dominio.entidades;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.tallerwebi.dominio.enums.Rubro;


public class Proveedor extends UsuarioAuth{
    private String razonSocial;        // Nombre legal de la empresa
    private Integer cuit;               // Identificador fiscal
    private Rubro rubro;              
    private String sitioWeb;           
    private String estado;             // Activo, Inactivo, Suspendido
    //private Double dolarReferencia;    // Último valor de dólar usado para precios


    /* Crearse la clase Producto
     private List<Producto> productos;       // Productos ofrecidos por el proveedor */
    private List<Cotizacion> cotizaciones;  // Cotizaciones realizadas por el proveedor


    public Proveedor(String email, String contrasenia){
        super(email,contrasenia);
        this.cotizaciones = new ArrayList<>();
    }


    public Proveedor() {
        //TODO Auto-generated constructor stub
    }


    public String getRazonSocial() {
        return razonSocial;
    }


    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }


    public Integer getCuit() {
        return cuit;
    }


    public void setCuit(Integer cuit) {
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


    public String getEstado() {
        return estado;
    }


    public void setEstado(String estado) {
        this.estado = estado;
    }


    public List<Cotizacion> getCotizaciones() {
        return cotizaciones;
    }


    public void setCotizaciones(List<Cotizacion> cotizaciones) {
        this.cotizaciones = cotizaciones;
    }

    



    

    
}


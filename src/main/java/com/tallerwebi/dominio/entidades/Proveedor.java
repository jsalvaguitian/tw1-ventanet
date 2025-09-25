package com.tallerwebi.dominio.entidades;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class Proveedor extends UsuarioAuth{
    private String razonSocial;        // Nombre legal de la empresa
    private String cuit;               // Identificador fiscal
    private String rubro;              
    private String sitioWeb;           
    private String estado;             // Activo, Inactivo, Suspendido
    private Double dolarReferencia;    // Último valor de dólar usado para precios


    /* Crearse la clase Producto
     private List<Producto> productos;       // Productos ofrecidos por el proveedor */
    private List<Cotizacion> cotizaciones;  // Cotizaciones realizadas por el proveedor


    public Proveedor(String email, String contrasenia){
        super(email,contrasenia);
        this.cotizaciones = new ArrayList<>();
    }

    

    
}


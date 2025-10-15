package com.tallerwebi.dominio.entidades;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class ProductoProveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Proveedor proveedor;

    @ManyToOne
    private Producto producto;

    private Double precioCotizado;            // opcional si cotizan después
    private Integer stock;
    private Boolean aceptaEnvio;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((proveedor == null) ? 0 : proveedor.hashCode());
        result = prime * result + ((producto == null) ? 0 : producto.hashCode());
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
        ProductoProveedor other = (ProductoProveedor) obj;
        if (proveedor == null) {
            if (other.proveedor != null)
                return false;
        } else if (!proveedor.equals(other.proveedor))
            return false;
        if (producto == null) {
            if (other.producto != null)
                return false;
        } else if (!producto.equals(other.producto))
            return false;
        return true;
    }


    


}

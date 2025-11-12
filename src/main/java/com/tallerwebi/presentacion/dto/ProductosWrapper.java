package com.tallerwebi.presentacion.dto;

import java.util.ArrayList;
import java.util.List;

public class ProductosWrapper {

    private List<ProductoImportDTO> productos;

    public ProductosWrapper() {

        productos = new ArrayList<>();
    }

    public List<ProductoImportDTO> getProductos() {
        return productos;
    }

    public void setProductos(List<ProductoImportDTO> productos) {
        this.productos = productos;
    }

    

}

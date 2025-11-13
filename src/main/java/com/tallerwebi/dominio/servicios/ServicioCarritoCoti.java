package com.tallerwebi.dominio.servicios;

import com.tallerwebi.dominio.entidades.CarritoCotizacion;
import com.tallerwebi.dominio.entidades.Cliente;

public interface ServicioCarritoCoti {

    CarritoCotizacion obtenerOCrearCarrito(Cliente cliente);

    void guardar(CarritoCotizacion carrito);

    
}

package com.tallerwebi.dominio.repositorios_interfaces;

import com.tallerwebi.dominio.entidades.CarritoCotizacion;
import com.tallerwebi.dominio.entidades.Cliente;

public interface RepositorioCarritoCoti {

    CarritoCotizacion buscarPorClienteYNoConfirmado(Cliente cliente);

    void guardar(CarritoCotizacion carrito);

    
}

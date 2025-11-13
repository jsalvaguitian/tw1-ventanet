package com.tallerwebi.dominio.servicios;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tallerwebi.dominio.entidades.CarritoCotizacion;
import com.tallerwebi.dominio.entidades.Cliente;
import com.tallerwebi.dominio.repositorios_interfaces.RepositorioCarritoCoti;

@Service
@Transactional
public class ServicioCarritoCotiImpl implements ServicioCarritoCoti{

    private RepositorioCarritoCoti carritoRepo;

    @Autowired
    public ServicioCarritoCotiImpl(RepositorioCarritoCoti carritoRepo) {
        this.carritoRepo = carritoRepo;
    }

    @Override
    public CarritoCotizacion obtenerOCrearCarrito(Cliente cliente) {
        CarritoCotizacion carrito = carritoRepo.buscarPorClienteYNoConfirmado(cliente);

        if(carrito == null){
            carrito = new CarritoCotizacion();
            carrito.setCliente(cliente);
            carritoRepo.guardar(carrito);
        }

        return carrito;
    }

    @Override
    public void guardar(CarritoCotizacion carrito) {
        carritoRepo.guardar(carrito);
    }

}

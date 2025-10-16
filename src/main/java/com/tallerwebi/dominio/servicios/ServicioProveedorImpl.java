package com.tallerwebi.dominio.servicios;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tallerwebi.dominio.entidades.Proveedor;
import com.tallerwebi.dominio.repositorios_interfaces.RepositorioProveedor;

@Service
@Transactional
public class ServicioProveedorImpl implements ServicioProveedorI{

    RepositorioProveedor repositorioProveedor;

    

    public ServicioProveedorImpl(RepositorioProveedor repositorioProveedor) {
        this.repositorioProveedor = repositorioProveedor;
    }



    @Override
    public List<Proveedor> obtenerTodosLosProveedoresActivos() {
        return repositorioProveedor.obtenerTodosLosNombresProveedoresActivos();
    }

}
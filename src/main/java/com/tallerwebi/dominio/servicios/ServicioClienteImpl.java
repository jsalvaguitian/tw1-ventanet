package com.tallerwebi.dominio.servicios;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tallerwebi.dominio.repositorios_interfaces.RepositorioCliente;

@Service
@Transactional
public class ServicioClienteImpl implements ServicioClienteI {

    RepositorioCliente repositorioCliente;

    @Autowired
    public ServicioClienteImpl(RepositorioCliente repositorioCliente) {
        this.repositorioCliente = repositorioCliente;
    }
    @Override
    public Integer contarClientes() {
        return repositorioCliente.contarClientes();
    }

}

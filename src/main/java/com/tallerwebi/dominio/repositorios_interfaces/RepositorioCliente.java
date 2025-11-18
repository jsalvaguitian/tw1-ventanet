package com.tallerwebi.dominio.repositorios_interfaces;

import com.tallerwebi.dominio.entidades.Cliente;

public interface RepositorioCliente {

    Integer contarClientes();

    Cliente buscarPorId(Long id);

}

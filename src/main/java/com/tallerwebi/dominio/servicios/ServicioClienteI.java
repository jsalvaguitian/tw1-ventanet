package com.tallerwebi.dominio.servicios;

import com.tallerwebi.dominio.entidades.Cliente;

public interface ServicioClienteI {

    Integer contarClientes();

    Cliente buscarPorId(Long id);

}

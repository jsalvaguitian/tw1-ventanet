package com.tallerwebi.dominio.repositorios_interfaces;

import com.tallerwebi.dominio.entidades.Usuario;

public interface RepositorioUsuario {

    Usuario buscarUsuario(String email, String password);
    void guardar(Usuario usuario);
    Usuario buscar(String email);
    void modificar(Usuario usuario);
    Usuario buscarPorEmailOCuit(String email, String cuit);
}


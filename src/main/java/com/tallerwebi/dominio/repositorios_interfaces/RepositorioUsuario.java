package com.tallerwebi.dominio.repositorios_interfaces;

import java.util.List;

import com.tallerwebi.dominio.entidades.Usuario;

public interface RepositorioUsuario {

    Usuario buscarUsuario(String email, String password);

    void guardar(Usuario usuario);

    // Usuario buscar(String email);
    void modificar(Usuario usuario);

    Usuario buscarPorMail(String email);

    Usuario buscarPorToken(String token);

    void actualizar(Usuario usuario);

    Integer contarUsuarios();

    List<Usuario> obtenerTodosLosUsuarios();

    Usuario buscarPorId(Long id);

    void eliminarUsuario(Usuario usuario);
}

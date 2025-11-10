package com.tallerwebi.dominio.servicios;

import java.io.IOException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.excepcion.ValorInvalido;
import com.tallerwebi.dominio.repositorios_interfaces.RepositorioUsuario;

@Service
@Transactional
public class ServicioPerfilImpl implements ServicioPerfil {

    private RepositorioUsuario repositorioUsuario;

    @Autowired
    public ServicioPerfilImpl(RepositorioUsuario repositorioUsuario) {
        this.repositorioUsuario = repositorioUsuario;
    }

    @Override
    @Transactional
    public boolean cambiarFotoPerfil(MultipartFile archivo, Usuario usuario) throws IOException {
        if (!archivo.isEmpty()) {
            usuario.setFotoPerfil(archivo.getBytes());
            repositorioUsuario.modificar(usuario);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public void actualizarPerfil(String nombre, String apellido, String username, String direccion, String telefono,
            Usuario usuarioActual) throws ValorInvalido {

        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ValorInvalido("El nombre no puede quedar vacio");
        }

        if (apellido == null || apellido.trim().isEmpty()) {
            throw new ValorInvalido("El apellido no puede quedar vacio");
        }

        if (telefono != null && !telefono.matches("\\d+")) {
            throw new ValorInvalido("El telfono solo debe contener numeros");
        }

        usuarioActual.setNombre(nombre);
        usuarioActual.setApellido(apellido);
        usuarioActual.setUsername(username);
        usuarioActual.setDireccion(direccion);
        usuarioActual.setTelefono(telefono);
        repositorioUsuario.actualizar(usuarioActual);

    }

}

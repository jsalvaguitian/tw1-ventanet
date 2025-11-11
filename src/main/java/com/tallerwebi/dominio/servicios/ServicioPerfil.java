package com.tallerwebi.dominio.servicios;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.excepcion.ValorInvalido;

public interface ServicioPerfil {
    boolean cambiarFotoPerfil(MultipartFile archivo, Usuario usuario) throws IOException;

    void actualizarPerfil(String nombre, String apellido, String username, String direccion, String telefono,
            Usuario usuarioActual) throws ValorInvalido;
}

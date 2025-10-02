package com.tallerwebi.dominio.servicios;

import org.springframework.web.multipart.MultipartFile;

import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.excepcion.ContraseniaInvalida;
import com.tallerwebi.dominio.excepcion.EmailInvalido;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;

public interface ServicioUsuario {

    Usuario consultarUsuario(String email, String password);

    void registrar(Usuario usuario) throws UsuarioExistente, ContraseniaInvalida, EmailInvalido;

    void registrarProveedor(Usuario proveedor, MultipartFile documento) throws UsuarioExistente, Exception;

}

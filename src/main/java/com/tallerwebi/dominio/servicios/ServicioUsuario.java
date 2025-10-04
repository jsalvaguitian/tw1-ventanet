package com.tallerwebi.dominio.servicios;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.tallerwebi.dominio.entidades.Proveedor;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.excepcion.ContraseniaInvalida;
import com.tallerwebi.dominio.excepcion.CuitInvalido;
import com.tallerwebi.dominio.excepcion.EmailInvalido;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;

public interface ServicioUsuario {

    Usuario consultarUsuario(String email, String password);

    void registrar(Usuario usuario) throws UsuarioExistente, ContraseniaInvalida, EmailInvalido;

    void registrarProveedor(Proveedor proveedor, MultipartFile documento) throws UsuarioExistente, ContraseniaInvalida, CuitInvalido, IOException;

}

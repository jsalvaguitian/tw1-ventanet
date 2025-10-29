package com.tallerwebi.dominio.servicios;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.tallerwebi.dominio.entidades.Cliente;
import com.tallerwebi.dominio.entidades.Proveedor;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.excepcion.ContraseniaInvalida;
import com.tallerwebi.dominio.excepcion.CuentaNoActivaException;
import com.tallerwebi.dominio.excepcion.CuentaPendienteException;
import com.tallerwebi.dominio.excepcion.CuentaRechazadaException;
import com.tallerwebi.dominio.excepcion.CuitInvalido;
import com.tallerwebi.dominio.excepcion.EmailInvalido;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import com.tallerwebi.dominio.excepcion.UsuarioInexistenteException;

public interface ServicioUsuario {

    Usuario iniciarSesion(String email, String password) throws UsuarioInexistenteException, CuentaNoActivaException, CuentaPendienteException, CuentaRechazadaException;

    void registrar(Cliente cliente) throws UsuarioExistente, ContraseniaInvalida, EmailInvalido;

    void registrarProveedor(Proveedor proveedor, MultipartFile documento)
            throws UsuarioExistente, ContraseniaInvalida, CuitInvalido, IOException;

    Usuario buscarPorMail(String email) throws UsuarioInexistenteException;
    

    boolean verificarToken(String token);

}

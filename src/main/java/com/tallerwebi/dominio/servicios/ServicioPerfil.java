package com.tallerwebi.dominio.servicios;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.tallerwebi.dominio.entidades.Proveedor;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.excepcion.ValorInvalido;

public interface ServicioPerfil {
        boolean cambiarFotoPerfil(MultipartFile archivo, Usuario usuario) throws IOException;

        void actualizarPerfil(String nombre, String apellido, String username, String direccion, Usuario usuarioActual)
                        throws ValorInvalido;

        void actualizarPerfilProveedor(String nombre, String apellido, String nombreUsuario, String direccion,
                        String telefono, String razonSocial, String ubicacion, Proveedor proveedorActual)
                        throws ValorInvalido;
}

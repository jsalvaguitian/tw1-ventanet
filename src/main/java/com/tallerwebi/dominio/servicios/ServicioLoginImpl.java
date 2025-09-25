package com.tallerwebi.dominio.servicios;

import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.excepcion.ContraseniaInvalida;
import com.tallerwebi.dominio.excepcion.EmailInvalido;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import com.tallerwebi.dominio.repositorios_interfaces.RepositorioUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service("servicioLogin")
@Transactional
public class ServicioLoginImpl implements ServicioLogin {

    private RepositorioUsuario repositorioUsuario;

    @Autowired
    public ServicioLoginImpl(RepositorioUsuario repositorioUsuario) {
        this.repositorioUsuario = repositorioUsuario;
    }

    @Override
    public Usuario consultarUsuario(String email, String password) {
        return repositorioUsuario.buscarUsuario(email, password);
    }

    @Override
    public void registrar(Usuario usuario) throws UsuarioExistente, ContraseniaInvalida, EmailInvalido {
        Usuario usuarioEncontrado = repositorioUsuario.buscar(usuario.getEmail());
        if (usuarioEncontrado != null) {
            throw new UsuarioExistente();
        }
        // Validar Contrasenia
        if (usuario.getPassword().length() < 8 ||
                !usuario.getPassword().matches(".*[A-Z].*") || // que tenga 1 mayuscula
                !usuario.getPassword().matches(".*[a-z].*") || // que tenga 1 minuscula
                !usuario.getPassword().matches(".*[^a-zA-Z0-9].*")) { // que tenga 1 simbolo
            throw new ContraseniaInvalida(
                    "La contraseña debe tener al menos 8 caracteres, 1 mayúscula, 1 minúscula y 1 símbolo.");
        }
        // Validar email
        if (!usuario.getEmail()
                .matches("[a-zA-Z0-9_]+([.][a-zA-Z0-9_]+)*@[a-zA-Z0-9_]+([.][a-zA-Z0-9_]+)*[.][a-zA-Z]{2,5}")) {
            // que haya 1 o mas caracteres + permite periodos (.) seguido de caracteres + @
            // + 1 o mas caracteres + periodo + de 2 a 5 mayusculas o minusculas
            throw new EmailInvalido("Correo electronico invalido");
        }
        repositorioUsuario.guardar(usuario);
    }

}

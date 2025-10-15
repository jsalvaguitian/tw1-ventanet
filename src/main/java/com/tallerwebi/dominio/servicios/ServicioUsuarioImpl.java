package com.tallerwebi.dominio.servicios;

import com.tallerwebi.dominio.entidades.Proveedor;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.excepcion.ContraseniaInvalida;
import com.tallerwebi.dominio.excepcion.CuitInvalido;
import com.tallerwebi.dominio.excepcion.EmailInvalido;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import com.tallerwebi.dominio.excepcion.UsuarioInexistenteException;
import com.tallerwebi.dominio.repositorios_interfaces.RepositorioProveedor;
import com.tallerwebi.dominio.repositorios_interfaces.RepositorioUsuario;
import com.tallerwebi.dominio.utils.PasswordUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import javax.transaction.Transactional;

@Service("servicioUsuario")
@Transactional
public class ServicioUsuarioImpl implements ServicioUsuario {

    private RepositorioUsuario repositorioUsuario;
    private RepositorioProveedor repositorioProveedor;
    private ServicioFileStorage fileStorageService = new ServicioFileStorage();

    @Autowired
    public ServicioUsuarioImpl(RepositorioUsuario repositorioUsuario, RepositorioProveedor repositorioProveedor) {
        this.repositorioUsuario = repositorioUsuario;
        this.repositorioProveedor = repositorioProveedor;
    }

    @Override
    public Usuario consultarUsuario(String email, String password) throws UsuarioInexistenteException {
        Usuario encontrado = repositorioUsuario.buscarPorMail(email);
        if (encontrado != null && PasswordUtil.verificar(password, encontrado.getPassword())) {
            return encontrado;
        }
        throw new UsuarioInexistenteException();
    }

    @Override
    public void registrar(Usuario usuario) throws UsuarioExistente, ContraseniaInvalida, EmailInvalido {
        String contraseniaHasheada = "";
        Usuario usuarioEncontrado = repositorioUsuario.buscarPorMail(usuario.getEmail());
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
        // hashear contrasenia antes de guardar
        contraseniaHasheada = PasswordUtil.hashear(usuario.getPassword());
        usuario.setPassword(contraseniaHasheada);
        repositorioUsuario.guardar(usuario);
    }

    /*
     * 
     */
    @Override
    public void registrarProveedor(Proveedor proveedor, MultipartFile documento)
            throws UsuarioExistente, CuitInvalido, ContraseniaInvalida, IOException {

        if (!validarCuit(proveedor.getCuit())) {
            throw new CuitInvalido();
        }

        if (repositorioUsuario.buscarPorMail(proveedor.getEmail()) != null) {
            throw new UsuarioExistente();
        }

        if (repositorioProveedor.buscarProveedorPorCuit(proveedor.getCuit()) != null) {
            throw new UsuarioExistente();
        }

        if (!validarContrasenia(proveedor.getPassword())) {
            throw new ContraseniaInvalida(
                    "La contraseña debe tener al menos 8 caracteres, 1 mayúscula, 1 minúscula y 1 símbolo.");
        }

        // Guardar documento del proveedor en el servidor
        if (!documento.isEmpty() || documento != null) {
            String path = fileStorageService.guardarArchivoImgOPdf(documento);
            proveedor.setDocumento(path);
        }

        String contraseniaHasheada = PasswordUtil.hashear(proveedor.getPassword());
        proveedor.setPassword(contraseniaHasheada);
        repositorioUsuario.guardar(proveedor);
    }

    private boolean validarContrasenia(String password) {
        if (password.length() >= 8 &&
                password.matches(".*[A-Z].*") && // que tenga 1 mayuscula
                password.matches(".*[a-z].*") && // que tenga 1 minuscula
                password.matches(".*[^a-zA-Z0-9].*")) // que tenga 1 simbolo
            return true;
        return false;
    }

    private boolean validarCuit(String cuit) {
        if (cuit == null || !cuit.matches("\\d{11}")) {
            return false;
        }
        int[] multiplicadores = { 5, 4, 3, 2, 7, 6, 5, 4, 3, 2 };
        int suma = 0;
        for (int i = 0; i < 10; i++) {
            int digito = Character.getNumericValue(cuit.charAt(i));
            suma += digito * multiplicadores[i];
        }

        int resto = suma % 11;

        int digitoVerificador = 11 - resto;
        if (digitoVerificador == 11) {
            digitoVerificador = 0;
        } else if (digitoVerificador == 10) {
            digitoVerificador = 9; // En algunos casos, el dígito verificador puede ser 9
        }

        int ultimoDigito = Character.getNumericValue(cuit.charAt(10));
        return digitoVerificador == ultimoDigito;

    }

    @Override
    public Usuario buscarPorMail(String email) throws UsuarioInexistenteException {
        Usuario usuarioEncontrado = repositorioUsuario.buscarPorMail(email);
        if (usuarioEncontrado == null) {
            throw new UsuarioInexistenteException();
        }

        return usuarioEncontrado;
    }

}

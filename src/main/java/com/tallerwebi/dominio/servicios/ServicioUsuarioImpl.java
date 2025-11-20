package com.tallerwebi.dominio.servicios;

import com.tallerwebi.dominio.entidades.Cliente;
import com.tallerwebi.dominio.entidades.Proveedor;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.enums.EstadoUsuario;
import com.tallerwebi.dominio.excepcion.ContraseniaInvalida;
import com.tallerwebi.dominio.excepcion.CuentaNoActivaException;
import com.tallerwebi.dominio.excepcion.CuentaPendienteException;
import com.tallerwebi.dominio.excepcion.CuentaRechazadaException;
import com.tallerwebi.dominio.excepcion.CuitInvalido;
import com.tallerwebi.dominio.excepcion.EmailInvalido;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import com.tallerwebi.dominio.excepcion.UsuarioInexistenteException;
import com.tallerwebi.dominio.repositorios_interfaces.RepositorioProveedor;
import com.tallerwebi.dominio.repositorios_interfaces.RepositorioUsuario;
import com.tallerwebi.dominio.utils.PasswordUtil;
import com.tallerwebi.presentacion.dto.UsuarioAdminDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

@Service("servicioUsuario")
@Transactional
public class ServicioUsuarioImpl implements ServicioUsuario {

    private RepositorioUsuario repositorioUsuario;
    private RepositorioProveedor repositorioProveedor;
    private ServicioFileStorage fileStorageService = new ServicioFileStorage();
    private ServicioEmail servicioEmail;

    @Autowired
    public ServicioUsuarioImpl(RepositorioUsuario repositorioUsuario, RepositorioProveedor repositorioProveedor,
            ServicioEmail servicioEmail) {
        this.repositorioUsuario = repositorioUsuario;
        this.repositorioProveedor = repositorioProveedor;
        this.servicioEmail = servicioEmail;
    }

    @Override
    public Usuario iniciarSesion(String email, String password) throws UsuarioInexistenteException,
            CuentaNoActivaException, CuentaPendienteException, CuentaRechazadaException {
        Usuario encontrado = repositorioUsuario.buscarPorMail(email);

        if (encontrado == null || !PasswordUtil.verificar(password, encontrado.getPassword())) {
            throw new UsuarioInexistenteException();
        }

        switch (encontrado.getEstado()) {
            case NO_ACTIVO:
                throw new CuentaNoActivaException();

            case PENDIENTE:
                throw new CuentaPendienteException();
            case RECHAZADO:
                throw new CuentaRechazadaException();
        }

        return encontrado;
    }

    @Override
    public void registrar(Cliente cliente) throws UsuarioExistente, ContraseniaInvalida, EmailInvalido {
        String contraseniaHasheada = "";
        Usuario usuarioEncontrado = repositorioUsuario.buscarPorMail(cliente.getEmail());
        if (usuarioEncontrado != null) {
            throw new UsuarioExistente();
        }
        // Validar Contrasenia
        if (cliente.getPassword().length() < 8 ||
                !cliente.getPassword().matches(".*[A-Z].*") || // que tenga 1 mayuscula
                !cliente.getPassword().matches(".*[a-z].*") || // que tenga 1 minuscula
                !cliente.getPassword().matches(".*[^a-zA-Z0-9].*")) { // que tenga 1 simbolo
            throw new ContraseniaInvalida(
                    "La contraseña debe tener al menos 8 caracteres, 1 mayúscula, 1 minúscula y 1 símbolo.");
        }
        // Validar email
        if (!cliente.getEmail()
                .matches("[a-zA-Z0-9_]+([.][a-zA-Z0-9_]+)*@[a-zA-Z0-9_]+([.][a-zA-Z0-9_]+)*[.][a-zA-Z]{2,5}")) {
            // que haya 1 o mas caracteres + permite periodos (.) seguido de caracteres + @
            // + 1 o mas caracteres + periodo + de 2 a 5 mayusculas o minusculas
            throw new EmailInvalido("Correo electronico invalido");
        }

        String token = UUID.randomUUID().toString();
        cliente.setTokenVerificacion(token);

        // Setear expiracion del token a 24 horas
        cliente.setExpiracionToken(LocalDateTime.now().plusHours(24));

        // hashear contrasenia antes de guardar
        contraseniaHasheada = PasswordUtil.hashear(cliente.getPassword());
        cliente.setPassword(contraseniaHasheada);
        cliente.setFechaCreacion(java.time.LocalDate.now());
        repositorioUsuario.guardar(cliente);

        // Enviar correo con el link de verificación
        servicioEmail.enviarEmailActivacion(cliente);
    }

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
        proveedor.setFechaCreacion(java.time.LocalDate.now());
        repositorioUsuario.guardar(proveedor);
        servicioEmail.enviarEmailActivacion(proveedor);
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

    @Override /// COMPLETAR
    public boolean verificarToken(String token) {

        Usuario usuario = repositorioUsuario.buscarPorToken(token);

        if (usuario == null) {
            return false;
        }

        if (usuario.getExpiracionToken().isBefore(LocalDateTime.now())) {
            return false; // token vencido
        }

        // Activar o cambiar estado según el tipo de usuario
        if (usuario instanceof Cliente) {
            usuario.setEstado(EstadoUsuario.ACTIVO);
        } /*
           * else if (usuario instanceof Proveedor) {
           * usuario.setEstado("PENDIENTE");
           * }
           */

        usuario.setTokenVerificacion(null);

        usuario.setExpiracionToken(null);

        repositorioUsuario.actualizar(usuario);
        return true;
    }

    @Override
    public Usuario buscarPorId(Long id) throws UsuarioInexistenteException {
        Usuario usuarioEncontrado = repositorioUsuario.buscarPorId(id);
        if (usuarioEncontrado == null) {
            throw new UsuarioInexistenteException();
        }
        return usuarioEncontrado;
    }

    @Override
    public Integer contarUsuarios() {
        return repositorioUsuario.contarUsuarios();
    }

    @Override
    public List<Usuario> obtenerTodosLosUsuarios() {
        return repositorioUsuario.obtenerTodosLosUsuarios();
    }

    @Override
    public void eliminarUsuario(Usuario usuario) {
        usuario.setActivo(false);
    }

    @Override
    public List<UsuarioAdminDTO> obtenerUsuariosParaAdmin() {

        List<Usuario> usuarios = this.obtenerTodosLosUsuarios();

        return usuarios.stream().map(usuario -> {
            UsuarioAdminDTO dto = new UsuarioAdminDTO();
            dto.setId(usuario.getId());
            dto.setNombre(usuario.getNombre());
            dto.setApellido(usuario.getApellido());
            dto.setEmail(usuario.getEmail());
            dto.setUsername(usuario.getUsername());
            dto.setTelefono(usuario.getTelefono());
            dto.setDireccion(usuario.getDireccion());
            dto.setFechaCreacion(usuario.getFechaCreacion());
            dto.setRol(usuario.getRol());
            dto.setActivo(usuario.getActivo());
            dto.setEstado(usuario.getEstado());
            dto.setFotoPerfil(usuario.getFotoPerfil());
            dto.setNombreMostrable(usuario.getNombreMostrable());

            // para proveedores
            if (usuario instanceof Proveedor) {
                Proveedor p = (Proveedor) usuario;

                dto.setCuit(p.getCuit());
                dto.setRubro(p.getRubro());
                dto.setDocumento(p.getDocumento());
                dto.setRazonSocial(p.getRazonSocial());
                dto.setUbicacion(p.getUbicacion());
            }

            return dto;
        }).collect(Collectors.toList());
    }

}
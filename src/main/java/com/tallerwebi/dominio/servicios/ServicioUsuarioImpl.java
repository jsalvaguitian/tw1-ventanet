package com.tallerwebi.dominio.servicios;

import com.tallerwebi.dominio.entidades.Proveedor;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.excepcion.ContraseniaInvalida;
import com.tallerwebi.dominio.excepcion.CuitInvalido;
import com.tallerwebi.dominio.excepcion.EmailInvalido;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import com.tallerwebi.dominio.repositorios_interfaces.RepositorioUsuario;
import com.tallerwebi.dominio.utils.PasswordUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;

@Service("servicioUsuario")
@Transactional
public class ServicioUsuarioImpl implements ServicioUsuario {

    private RepositorioUsuario repositorioUsuario;
    private ServicioFileStorage fileStorageService;

    @Autowired
    public ServicioUsuarioImpl(RepositorioUsuario repositorioUsuario, ServicioFileStorage fileStorageService) {
        this.fileStorageService = fileStorageService;
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

    /*
     * ToDO
     * Cuit invalido usar el algoritmo de validacion de AFIP
     * Usuario existente (proveedor)
     * Contrasenia invalida
     * Solo se aceptan archivos pdf, jpg, png, jpeg
     * Solo se aceptan archivos de hasta 3MB
     */
    @Override
    public void registrarProveedor(Usuario proveedor, MultipartFile documento) throws Exception {
        
        //validar con el algoritmo de AFIP MOD 11
        if(!validarCUIT(proveedor.getCuit())) {
            throw new CuitInvalido();
        }
        
        //validar si ya existe un usuario con ese email o cuit
        Usuario proveedorEncontrado = repositorioUsuario.buscarPorEmailOCuit(proveedor.getEmail(), proveedor.getCuit());

        if(proveedorEncontrado != null) {
            throw new UsuarioExistente();
        }

        //Guardar documento del proveedor en el servidor
        if(!documento.isEmpty()){
            String path = fileStorageService.guardarArchivo(documento);
            proveedor.setPathDocumentacionAFIP(path);
        }

        //validar contrasenia y hashearla
        if(validarContrasenia(proveedor.getPassword())) {

            String contraseniaHasheada = PasswordUtil.hashear(proveedor.getPassword());
            proveedor.setPassword(contraseniaHasheada);
            repositorioUsuario.guardar(proveedor);
        }
        
        /*if(documeno.isEmpty() || documento.getSize() > 3 * 1024 * 1024) {
            throw new Exception("El documento es obligatorio y debe ser menor a 3MB");
        }*/

    }

    private boolean validarContrasenia(String password) throws ContraseniaInvalida {
        if(password.length() >= 8 &&
                password.matches(".*[A-Z].*") && // que tenga 1 mayuscula
                password.matches(".*[a-z].*") && // que tenga 1 minuscula
                password.matches(".*[^a-zA-Z0-9].*")) // que tenga 1 simbolo
            return true;
        throw new ContraseniaInvalida("La contraseña debe tener al menos 8 caracteres, 1 mayúscula, 1 minúscula y 1 símbolo.");
    }

    private boolean validarCUIT(String cuit) {
        if(cuit == null || !cuit.matches("\\d{11}")) {
            return false; // debe tener 11 digitos
        }

        int[] multiplicadores = {5,4,3,2,7,6,5,4,3,2};
        int suma = 0;

        for(int i=0; i<10; i++) {
            int digito = Character.getNumericValue(cuit.charAt(i));
            suma += digito * multiplicadores[i];
        }

        int resto = suma % 11;
        int digitoVerificador = 11 - resto;

        if(digitoVerificador == 11) {
            digitoVerificador = 0;
        } else if(digitoVerificador == 10) {
            digitoVerificador = 9;
        }

        int ultimoDigito = Character.getNumericValue(cuit.charAt(10));
        return digitoVerificador == ultimoDigito;
    }

}

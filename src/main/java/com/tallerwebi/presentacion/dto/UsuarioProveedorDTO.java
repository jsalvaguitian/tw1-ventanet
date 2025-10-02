package com.tallerwebi.presentacion.dto;

import org.springframework.web.multipart.MultipartFile;

import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.enums.Rol;
import com.tallerwebi.dominio.enums.Rubro;

public class UsuarioProveedorDTO {
    private String email;
    private String contrasenia;

    //datos del proveedor
    private String razonSocial;
    private String cuit;
    private Rubro rubro;
    
    private MultipartFile documento;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getCuit() {
        return cuit;
    }

    public void setCuit(String cuit) {
        this.cuit = cuit;
    }

    public Rubro getRubro() {
        return rubro;
    }

    public void setRubro(Rubro rubro) {
        this.rubro = rubro;
    }

    public MultipartFile getDocumento() {
        return documento;
    }

    public void setDocumento(MultipartFile documento) {
        this.documento = documento;
    }

    //Obtener la entidad Proveedor a partir del DTO
    public Usuario obtenerEntidad() {

        Usuario proveedor = new Usuario();
        
        proveedor.setRolUsuario(Rol.PROVEEDOR);

        proveedor.setRazonSocial(this.razonSocial);
        proveedor.setCuit(this.cuit);
        proveedor.setRubro(this.rubro);

        //se seteara el path documentacion en el servicio
        return proveedor;  
    }

    



}

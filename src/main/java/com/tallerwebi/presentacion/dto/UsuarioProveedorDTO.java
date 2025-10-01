package com.tallerwebi.presentacion.dto;

import org.springframework.web.multipart.MultipartFile;

import com.tallerwebi.dominio.entidades.Proveedor;
import com.tallerwebi.dominio.enums.Rubro;

public class UsuarioProveedorDTO {
    private String email;
    private String contrasenia;

    //datos del proveedor
    private String razonSocial;
    private Integer cuit;
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

    public Integer getCuit() {
        return cuit;
    }

    public void setCuit(Integer cuit) {
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
    public Proveedor obtenerEntidad() {

        Proveedor proveedor = new Proveedor();
        proveedor.setRazonSocial(this.razonSocial);
        proveedor.setCuit(this.cuit);
        proveedor.setRubro(this.rubro);

        //falta path documentacion
        return proveedor;  
    }

    



}

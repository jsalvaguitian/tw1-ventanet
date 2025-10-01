package com.tallerwebi.presentacion.dto;

import javax.persistence.criteria.CriteriaBuilder.In;

public class UsuarioDto {
    private String email;
    private String contrasenia;

    private String repeatContrasenia;
    //datos del proveedor
    private String razonSocial;
    private Integer cuit;
    private String rubro;
    
    public UsuarioDto() {
    }

    public UsuarioDto(String mail, String contrasenia) {
        this.email = mail;
        this.contrasenia = contrasenia;    
    }

    

    public String getRepeatContrasenia() {
        return repeatContrasenia;
    }

    public void setRepeatContrasenia(String repeatContrasenia) {
        this.repeatContrasenia = repeatContrasenia;
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

    public String getRubro() {
        return rubro;
    }

    public void setRubro(String rubro) {
        this.rubro = rubro;
    }

    public String getEmail() {
        return email;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }


}

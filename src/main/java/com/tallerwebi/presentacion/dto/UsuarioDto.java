package com.tallerwebi.presentacion.dto;

import javax.persistence.criteria.CriteriaBuilder.In;

public class UsuarioDto {
    private String email;
    private String contrasenia;

    public UsuarioDto() {
    }

    public UsuarioDto(String mail, String contrasenia) {
        this.email = mail;
        this.contrasenia = contrasenia;    
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

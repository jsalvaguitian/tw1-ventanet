package com.tallerwebi.presentacion.dto;

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

    


}

package com.tallerwebi.presentacion.dto;

public class UsuarioDto {
    private String usernameOrMail;
    private String contrasenia;



    
    public UsuarioDto() {
    }



    public UsuarioDto(String userOrMail, String contrasenia) {
        this.usernameOrMail = userOrMail;
        this.contrasenia = contrasenia;
        
    }



    public String getUsernameOrMail() {
        return usernameOrMail;
    }



    public String getContrasenia() {
        return contrasenia;
    }

    


}

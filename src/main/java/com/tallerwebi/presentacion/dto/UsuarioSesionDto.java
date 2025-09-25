package com.tallerwebi.presentacion.dto;

public class UsuarioSesionDto {
    private Long id;
    private String username;
    private String rol;

    
    public UsuarioSesionDto(Long id, String username, String rol) {
        this.id = id;
        this.username = username;
        this.rol = rol;
    }

    public UsuarioSesionDto() {
        //TODO Auto-generated constructor stub
    }

    public Long getId() {
        return id;
    }

    public String getRol() {
        return rol;
    }

    public String getUsername() {
        return username;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    

}

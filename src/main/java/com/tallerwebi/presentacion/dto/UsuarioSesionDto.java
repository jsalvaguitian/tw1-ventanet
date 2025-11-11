package com.tallerwebi.presentacion.dto;

public class UsuarioSesionDto {
    private Long id;
    private String username;
    private String rol;
    private String nombre;
    private String apellido;

    public UsuarioSesionDto(Long id, String username, String rol, String nombre, String apellido) {
        this.id = id;
        this.username = username;
        this.rol = rol;
        this.nombre = nombre;
        this.apellido = apellido;

    }

    public UsuarioSesionDto() {
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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }
}

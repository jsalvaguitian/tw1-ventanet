package com.tallerwebi.dominio.entidades;

import java.time.LocalDate;

public abstract class UsuarioAuth {

    private Long id;
    private String nombre;
    private String apellido;
    private String username;
    private String email;
    private String contrasenia;
    private String telefono;
    private String direccion;
    private LocalDate fechaCreacion;
    private String rol;

    

    public UsuarioAuth() {
    }
    
    public UsuarioAuth(String email, String contrasenia) {
        this.email = email;
        this.contrasenia = contrasenia;
        this.rol = this.getClass().getName();
    }

    

    public UsuarioAuth(Long id, String email, String contrasenia) {
        this.id = id;
        this.email = email;
        this.contrasenia = contrasenia;
    }

    public UsuarioAuth(Long id, String nombre, String apellido, String username, String email, String contrasenia,
            String telefono, String direccion, LocalDate fechaCreacion) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.username = username;
        this.email = email;
        this.contrasenia = contrasenia;
        this.telefono = telefono;
        this.direccion = direccion;
        this.fechaCreacion = fechaCreacion;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
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
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
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
    public String getTelefono() {
        return telefono;
    }
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    public String getDireccion() {
        return direccion;
    }
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }
    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getRol() {
        return rol;
    }

}

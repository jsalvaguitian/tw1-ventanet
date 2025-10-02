package com.tallerwebi.dominio.entidades;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.tallerwebi.dominio.enums.EstadoProveedor;
import com.tallerwebi.dominio.enums.Rol;
import com.tallerwebi.dominio.enums.Rubro;

@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;

    @Column(nullable = false, unique = true) // email unico y no se permiten nulos
    private String email;
    @Column(nullable = false)
    private String password;
    private String rol;
    private Boolean activo = false;

    // -------- Proveedor -------------------
    @Enumerated(EnumType.STRING)
    //@Column(nullable = false)
    private Rol rolUsuario;

    @Enumerated(EnumType.STRING)
    private EstadoProveedor estadoProveedor;
    private String cuit;

    @Enumerated(EnumType.STRING)
    private Rubro rubro;
    private String razonSocial;
    private String pathDocumentacionAFIP;
    // ------------------------------------

    public Usuario() {
    }

    public Rol getRolUsuario() {
        return rolUsuario;
    }

    public void setRolUsuario(Rol rolUsuario) {
        this.rolUsuario = rolUsuario;
    }

    public EstadoProveedor getEstadoProveedor() {
        return estadoProveedor;
    }

    public void setEstadoProveedor(EstadoProveedor estadoProveedor) {
        this.estadoProveedor = estadoProveedor;
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

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getPathDocumentacionAFIP() {
        return pathDocumentacionAFIP;
    }

    public void setPathDocumentacionAFIP(String pathDocumentacionAFIP) {
        this.pathDocumentacionAFIP = pathDocumentacionAFIP;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public boolean activo() {
        return activo;
    }

    public void activar() {
        activo = true;
    }
}

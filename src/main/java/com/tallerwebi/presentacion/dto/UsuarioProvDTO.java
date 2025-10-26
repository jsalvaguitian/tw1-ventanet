package com.tallerwebi.presentacion.dto;

import org.springframework.web.multipart.MultipartFile;

import com.tallerwebi.dominio.entidades.Proveedor;
import com.tallerwebi.dominio.enums.EstadoUsuario;
import com.tallerwebi.dominio.enums.Rubro;

public class UsuarioProvDTO {
    private Long id;
    private String email;
    private String password;
    private String cuit;
    private String razonSocial;
    private Rubro rubro;
    private EstadoUsuario estado;

    private MultipartFile documento;

    private String documentoPath;
    
    public UsuarioProvDTO(Long id, String razonSocial) {
        this.id = id;
        this.razonSocial = razonSocial;
    }

    public UsuarioProvDTO() {
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
    public String getCuit() {
        return cuit;
    }
    public void setCuit(String cuit) {
        this.cuit = cuit;
    }
    public String getRazonSocial() {
        return razonSocial;
    }
    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
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

    public Proveedor obtenerEntidad() {
        Proveedor proveedor = new Proveedor();
        proveedor.setEmail(this.email);
        proveedor.setPassword(this.password);;
        proveedor.setCuit(this.cuit);
        proveedor.setRazonSocial(this.razonSocial);
        proveedor.setRubro(this.rubro);
        return proveedor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EstadoUsuario getEstado() {
        return estado;
    }

    public void setEstado(EstadoUsuario estado) {
        this.estado = estado;
    }

    public String getDocumentoPath() {
        return documentoPath;
    }

    public void setDocumentoPath(String documentoPath) {
        this.documentoPath = documentoPath;
    }
    
}

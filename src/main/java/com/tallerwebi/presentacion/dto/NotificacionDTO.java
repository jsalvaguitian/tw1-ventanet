package com.tallerwebi.presentacion.dto;

import com.tallerwebi.dominio.entidades.Notificacion;

public class NotificacionDTO {
    private Long id;
    private String mensaje;
    private String url;
    private boolean leida;
    private String fechaFormato;

    public NotificacionDTO(Notificacion n) {
        this.id = n.getId();
        this.mensaje = n.getMensaje();
        this.url = n.getUrl();
        this.leida = n.isLeida();
        this.fechaFormato = n.getFechaCreacion().toString();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isLeida() {
        return leida;
    }

    public void setLeida(boolean leida) {
        this.leida = leida;
    }

    public String getFechaFormato() {
        return fechaFormato;
    }

    public void setFechaFormato(String fechaFormato) {
        this.fechaFormato = fechaFormato;
    }

}

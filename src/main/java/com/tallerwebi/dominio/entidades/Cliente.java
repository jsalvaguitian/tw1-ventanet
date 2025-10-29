package com.tallerwebi.dominio.entidades;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.tallerwebi.dominio.enums.EstadoUsuario;

@Entity
@DiscriminatorValue("CLIENTE")
public class Cliente extends Usuario {

    public Cliente() {
        super();
        this.estado = EstadoUsuario.NO_ACTIVO;
    }

}
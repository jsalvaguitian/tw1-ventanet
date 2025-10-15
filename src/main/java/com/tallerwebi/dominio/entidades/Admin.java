package com.tallerwebi.dominio.entidades;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("ADMIN")
public class Admin extends Usuario{

    public Admin() {}

}

package com.tallerwebi.dominio.repositorios_interfaces;

import com.tallerwebi.dominio.entidades.ResetearPasswordToken;

public interface RepositorioTokenRecuperarPassword {

    void eliminar(ResetearPasswordToken tokenRecibido);

    void guardar(ResetearPasswordToken tokenEntidad);

    ResetearPasswordToken buscarPorToken(String token);

}

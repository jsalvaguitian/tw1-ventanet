package com.tallerwebi.dominio.repositorios_interfaces;

import java.util.List;

import com.tallerwebi.dominio.entidades.TipoDeVidrio;

public interface RepositorioTipoDeVidrio {

    TipoDeVidrio buscarPorNombre(String tipoVidrio);

    void guardar(TipoDeVidrio tipoDeVidrio);

    List<TipoDeVidrio> obtener();

    
}

package com.tallerwebi.dominio.servicios;

import java.util.List;
import com.tallerwebi.dominio.entidades.Provincia;

public interface ServicioProvincia {
    List<Provincia> obtener();

    void crearProvincia(Provincia provincia);

    Provincia obtenerPorId(Long id);

    void actualizar(Provincia provincia);

    void eliminar(Long id);
}

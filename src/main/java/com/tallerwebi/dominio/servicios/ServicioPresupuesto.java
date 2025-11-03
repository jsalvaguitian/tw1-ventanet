package com.tallerwebi.dominio.servicios;

import java.util.List;

import com.tallerwebi.dominio.entidades.Presupuesto;
import com.tallerwebi.dominio.excepcion.ProductoExistente;

public interface ServicioPresupuesto {
    List<Presupuesto> obtener();

    Presupuesto crearPresupuesto(Presupuesto presupuesto) throws ProductoExistente;

    Presupuesto obtenerPorId(Long id);

    void eliminar(Long id);

}

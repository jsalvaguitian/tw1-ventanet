package com.tallerwebi.dominio.repositorios_interfaces;

import java.util.List;
import com.tallerwebi.dominio.entidades.Presupuesto;

public interface RepositorioPresupuesto {
    Presupuesto guardar(Presupuesto presupuesto);
    List<Presupuesto> obtenerPorIdCliente(Long id);
}

package com.tallerwebi.dominio.servicios;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tallerwebi.dominio.entidades.Presupuesto;
import com.tallerwebi.dominio.entidades.PresupuestoItem;
import com.tallerwebi.dominio.excepcion.ProductoExistente;
import com.tallerwebi.dominio.repositorios_interfaces.RepositorioPresupuesto;

@Service
public class ServicioPresupuestoImpl implements ServicioPresupuesto {

    private final RepositorioPresupuesto repositorioPresupuesto;
    private final com.tallerwebi.dominio.repositorios_interfaces.RepositorioPresupuestoItem repositorioPresupuestoItem;

    public ServicioPresupuestoImpl(RepositorioPresupuesto repositorioPresupuesto, com.tallerwebi.dominio.repositorios_interfaces.RepositorioPresupuestoItem repositorioPresupuestoItem) {
        this.repositorioPresupuesto = repositorioPresupuesto;
        this.repositorioPresupuestoItem = repositorioPresupuestoItem;
    }

    @Override
    public java.util.List<Presupuesto> obtener() {
        throw new UnsupportedOperationException("No implementado aún");
    }

    @Override
    @Transactional
    public Presupuesto crearPresupuesto(Presupuesto presupuesto) throws ProductoExistente {
        // aquí podrías validar items/negocio antes de persistir
        Presupuesto saved = repositorioPresupuesto.guardar(presupuesto);
        // si la entidad Presupuesto tiene items ya ligados, persistirlos
        try {
            if (saved.getItems() != null) {
                for (PresupuestoItem it : saved.getItems()) {
                    it.setPresupuesto(saved);
                    repositorioPresupuestoItem.guardar(it);
                }
            }
        } catch (Exception e) {
            // ignorar por ahora; en futuro mejorar manejo
        }
        return saved;
    }

    @Override
    public Presupuesto obtenerPorId(Long id) {
        throw new UnsupportedOperationException("No implementado aún");
    }

    @Override
    public void eliminar(Long id) {
        throw new UnsupportedOperationException("No implementado aún");
    }
}

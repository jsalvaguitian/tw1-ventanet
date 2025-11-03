package com.tallerwebi.infraestructura;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.tallerwebi.dominio.entidades.Presupuesto;
import com.tallerwebi.dominio.repositorios_interfaces.RepositorioPresupuesto;

@Repository("repositorioPresupuesto")
public class RepositorioPresupuestoImpl implements RepositorioPresupuesto {

    private final SessionFactory sessionFactory;

    @Autowired
    public RepositorioPresupuestoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Presupuesto guardar(Presupuesto presupuesto) {
        final Session session = sessionFactory.getCurrentSession();
        session.save(presupuesto);
        return presupuesto;
    }
}

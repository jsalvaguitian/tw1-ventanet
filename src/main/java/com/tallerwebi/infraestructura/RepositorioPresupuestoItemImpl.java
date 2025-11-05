package com.tallerwebi.infraestructura;

import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.tallerwebi.dominio.repositorios_interfaces.RepositorioPresupuestoItem;
import com.tallerwebi.dominio.entidades.PresupuestoItem;

@Repository("repositorioPresupuestoItem")
public class RepositorioPresupuestoItemImpl implements RepositorioPresupuestoItem {

    private final SessionFactory sessionFactory;

    @Autowired
    public RepositorioPresupuestoItemImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public PresupuestoItem guardar(PresupuestoItem item) {
        final Session session = sessionFactory.getCurrentSession();
        session.save(item);
        return item;
    }
}

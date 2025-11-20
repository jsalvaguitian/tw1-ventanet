package com.tallerwebi.infraestructura;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.tallerwebi.dominio.entidades.Marca;
import com.tallerwebi.dominio.entidades.MedioDePago;
import com.tallerwebi.dominio.repositorios_interfaces.RepositorioMedioDePago;

@Repository("repositorioMedioDePago")
public class RepositorioMedioDePagoImpl implements RepositorioMedioDePago {
    private SessionFactory sessionFactory;

    @Autowired
    public RepositorioMedioDePagoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<MedioDePago> obtenerTodos() {
        return sessionFactory.getCurrentSession()
                .createQuery("from MedioDePago", MedioDePago.class)
                .list();
    }

    @Override
    public MedioDePago buscarPorId(Long medioId) {
        final Session session = sessionFactory.getCurrentSession();
        return (MedioDePago) session.createCriteria(MedioDePago.class)
                .add(Restrictions.eq("id", medioId))
                .uniqueResult();
    }

}

package com.tallerwebi.infraestructura;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.tallerwebi.dominio.entidades.Alto;
import com.tallerwebi.dominio.entidades.Color;
import com.tallerwebi.dominio.repositorios_interfaces.RepositorioGenerico;

@Repository("repositorioAlto")
public class RepositorioAltoImpl implements RepositorioGenerico<Alto> {
    // private final Map<Long, Producto> database;
    // private static Long proximoId;
    private SessionFactory sessionFactory;

    @Autowired
    public RepositorioAltoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Alto> obtener() {
        return sessionFactory.getCurrentSession()
                .createQuery("from Alto", Alto.class)
                .list();
    }

    @Override
    public Alto obtener(Long id) {
        final Session session = sessionFactory.getCurrentSession();
        return (Alto) session.createCriteria(Alto.class)
                .add(Restrictions.eq("id", id))
                .uniqueResult();
    }

    @Override
    public Boolean guardar(Alto item) {
        sessionFactory.getCurrentSession().save(item);
        return true;
    }

    @Override
    public Boolean actualizar(Alto item) {
        sessionFactory.getCurrentSession().update(item);
        return true;
    }

    @Override
    public void eliminar(Long id) {
        Alto presentacion = (Alto) sessionFactory.getCurrentSession().createCriteria(Alto.class)
                .add(Restrictions.eq("id", id))
                .uniqueResult();

        if (presentacion != null) {
            sessionFactory.getCurrentSession().remove(presentacion);
        }
    }

    public Alto buscarPorNombre(String alto) {

        return (Alto) sessionFactory.getCurrentSession()
                .createCriteria(Alto.class)
                .add(Restrictions.eq("nombre", alto))
                .uniqueResult();

    }

}

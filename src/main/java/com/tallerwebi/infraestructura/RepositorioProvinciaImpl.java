package com.tallerwebi.infraestructura;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.tallerwebi.dominio.entidades.Provincia;
import com.tallerwebi.dominio.repositorios_interfaces.RepositorioGenerico;


@Repository("repositorioProvincia")
public class RepositorioProvinciaImpl implements RepositorioGenerico<Provincia> {
    // private final Map<Long, Producto> database;
    // private static Long proximoId;
    private SessionFactory sessionFactory;

    @Autowired
    public RepositorioProvinciaImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Provincia obtener(Long id) {
        final Session session = sessionFactory.getCurrentSession();
        return (Provincia) session.createCriteria(Provincia.class)
                .add(Restrictions.eq("id_provincia", id))
                .uniqueResult();
    }

    @Override
    public List<Provincia> obtener() {
        return sessionFactory.getCurrentSession()
                .createQuery("from Provincia", Provincia.class)
                .list();
    }

    @Override
    public Boolean guardar(Provincia item) {
        sessionFactory.getCurrentSession().save(item);
        return true;
    }

    @Override
    public Boolean actualizar(Provincia item) {
        sessionFactory.getCurrentSession().update(item);
        return true;
    }

    @Override
    public void eliminar(Long id) {
        Provincia presentacion = (Provincia) sessionFactory.getCurrentSession().createCriteria(Provincia.class)
                .add(Restrictions.eq("id_provincia", id))
                .uniqueResult();

        if (presentacion != null) {
            sessionFactory.getCurrentSession().remove(presentacion);
        }
    }
}

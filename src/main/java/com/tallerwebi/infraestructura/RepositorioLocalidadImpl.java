package com.tallerwebi.infraestructura;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.tallerwebi.dominio.entidades.Localidad;
import com.tallerwebi.dominio.repositorios_interfaces.RepositorioGenerico;


@Repository("repositorioLocalidad")
public class RepositorioLocalidadImpl implements RepositorioGenerico<Localidad> {
    // private final Map<Long, Producto> database;
    // private static Long proximoId;
    private SessionFactory sessionFactory;

    @Autowired
    public RepositorioLocalidadImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Localidad obtener(Long id) {
        final Session session = sessionFactory.getCurrentSession();
        return (Localidad) session.createCriteria(Localidad.class)
                .add(Restrictions.eq("id_localidad", id))
                .uniqueResult();
    }

    @Override
    public List<Localidad> obtener() {
        return sessionFactory.getCurrentSession()
                .createQuery("from Localidad", Localidad.class)
                .list();
    }

    @Override
    public Boolean guardar(Localidad item) {
        sessionFactory.getCurrentSession().save(item);
        return true;
    }

    @Override
    public Boolean actualizar(Localidad item) {
        sessionFactory.getCurrentSession().update(item);
        return true;
    }

    @Override
    public void eliminar(Long id) {
        Localidad presentacion = (Localidad) sessionFactory.getCurrentSession().createCriteria(Localidad.class)
                .add(Restrictions.eq("id_localidad", id))
                .uniqueResult();

        if (presentacion != null) {
            sessionFactory.getCurrentSession().remove(presentacion);
        }
    }
}

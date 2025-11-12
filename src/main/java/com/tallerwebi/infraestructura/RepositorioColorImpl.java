package com.tallerwebi.infraestructura;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.tallerwebi.dominio.entidades.Ancho;
import com.tallerwebi.dominio.entidades.Color;
import com.tallerwebi.dominio.repositorios_interfaces.RepositorioGenerico;

@Repository("repositorioColor")
public class RepositorioColorImpl implements RepositorioGenerico<Color> {
    // private final Map<Long, Producto> database;
    // private static Long proximoId;
    private SessionFactory sessionFactory;

    @Autowired
    public RepositorioColorImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Color> obtener() {
        return sessionFactory.getCurrentSession()
                .createQuery("from Color", Color.class)
                .list();
    }

    @Override
    public Color obtener(Long id) {
        final Session session = sessionFactory.getCurrentSession();
        return (Color) session.createCriteria(Color.class)
                .add(Restrictions.eq("id", id))
                .uniqueResult();
    }

    @Override
    public Boolean guardar(Color item) {
        sessionFactory.getCurrentSession().save(item);
        return true;
    }

    @Override
    public Boolean actualizar(Color item) {
        sessionFactory.getCurrentSession().update(item);
        return true;
    }

    @Override
    public void eliminar(Long id) {
        Color presentacion = (Color) sessionFactory.getCurrentSession().createCriteria(Color.class)
                .add(Restrictions.eq("id", id))
                .uniqueResult();

        if (presentacion != null) {
            sessionFactory.getCurrentSession().remove(presentacion);
        }
    }

    public Color buscarPorNombre(String color) {
        return (Color) sessionFactory.getCurrentSession()
                .createCriteria(Color.class)
                .add(Restrictions.eq("nombre", color))
                .uniqueResult();
    }

}

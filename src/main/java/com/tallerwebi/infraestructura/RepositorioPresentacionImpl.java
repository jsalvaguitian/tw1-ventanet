package com.tallerwebi.infraestructura;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.tallerwebi.dominio.entidades.Alto;
import com.tallerwebi.dominio.entidades.Presentacion;
import com.tallerwebi.dominio.repositorios_interfaces.RepositorioGenerico;

@Repository("repositorioPresentacion")
public class RepositorioPresentacionImpl implements RepositorioGenerico<Presentacion> {
    // private final Map<Long, Producto> database;
    // private static Long proximoId;
    private SessionFactory sessionFactory;

    @Autowired
    public RepositorioPresentacionImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Presentacion obtener(Long id) {
        final Session session = sessionFactory.getCurrentSession();
        return (Presentacion) session.createCriteria(Presentacion.class)
                .add(Restrictions.eq("id", id))
                .uniqueResult();
    }

    @Override
    public List<Presentacion> obtener() {
        return sessionFactory.getCurrentSession()
                .createQuery("from Presentacion", Presentacion.class)
                .list();
    }

    @Override
    public Boolean guardar(Presentacion item) {
        sessionFactory.getCurrentSession().save(item);
        return true;
    }

    @Override
    public Boolean actualizar(Presentacion item) {
        sessionFactory.getCurrentSession().update(item);
        return true;
    }

    @Override
    public void eliminar(Long id) {
        Presentacion presentacion = (Presentacion) sessionFactory.getCurrentSession().createCriteria(Presentacion.class)
                .add(Restrictions.eq("id", id))
                .uniqueResult();

        if (presentacion != null) {
            sessionFactory.getCurrentSession().remove(presentacion);
        }
    }

    public Presentacion buscarPorNombre(String presentacion) {
        return (Presentacion) sessionFactory.getCurrentSession()
                .createCriteria(Presentacion.class)
                .add(Restrictions.eq("descripcion", presentacion))
                .uniqueResult();
    }
}

package com.tallerwebi.infraestructura;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.tallerwebi.dominio.entidades.Partido;
import com.tallerwebi.dominio.repositorios_interfaces.RepositorioGenerico;


@Repository("repositorioPartido")
public class RepositorioPartidoImpl implements RepositorioGenerico<Partido> {
    // private final Map<Long, Producto> database;
    // private static Long proximoId;
    private SessionFactory sessionFactory;

    @Autowired
    public RepositorioPartidoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Partido obtener(Long id) {
        final Session session = sessionFactory.getCurrentSession();
        return (Partido) session.createCriteria(Partido.class)
                .add(Restrictions.eq("id_partido", id))
                .uniqueResult();
    }

    @Override
    public List<Partido> obtener() {
        return sessionFactory.getCurrentSession()
                .createQuery("from Partido", Partido.class)
                .list();
    }

    @Override
    public Boolean guardar(Partido item) {
        sessionFactory.getCurrentSession().save(item);
        return true;
    }

    @Override
    public Boolean actualizar(Partido item) {
        sessionFactory.getCurrentSession().update(item);
        return true;
    }

    @Override
    public void eliminar(Long id) {
        Partido presentacion = (Partido) sessionFactory.getCurrentSession().createCriteria(Partido.class)
                .add(Restrictions.eq("id_partido", id))
                .uniqueResult();

        if (presentacion != null) {
            sessionFactory.getCurrentSession().remove(presentacion);
        }
    }

        public List<Partido> obtenerPorIdDeLocalidad(Long localidad_id) {
        return sessionFactory.getCurrentSession()
                .createQuery("from Partido where localidad_id = :localidad_id", Partido.class)
                .setParameter("localidad_id", localidad_id)
                .list();
    }
}

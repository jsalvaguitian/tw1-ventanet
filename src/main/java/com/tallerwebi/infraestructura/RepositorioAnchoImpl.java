package com.tallerwebi.infraestructura;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.tallerwebi.dominio.entidades.Alto;
import com.tallerwebi.dominio.entidades.Ancho;
import com.tallerwebi.dominio.repositorios_interfaces.RepositorioGenerico;

@Repository("repositorioAncho")
public class RepositorioAnchoImpl implements RepositorioGenerico<Ancho> {
    // private final Map<Long, Producto> database;
    // private static Long proximoId;
    private SessionFactory sessionFactory;

    @Autowired
    public RepositorioAnchoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Ancho> obtener() {
        return sessionFactory.getCurrentSession()
                .createQuery("from Ancho", Ancho.class)
                .list();
    }

    @Override
    public Ancho obtener(Long id) {
        final Session session = sessionFactory.getCurrentSession();
        return (Ancho) session.createCriteria(Ancho.class)
                .add(Restrictions.eq("id", id))
                .uniqueResult();
    }

    @Override
    public Boolean guardar(Ancho item) {
        sessionFactory.getCurrentSession().save(item);
        return true;
    }

    @Override
    public Boolean actualizar(Ancho item) {
        sessionFactory.getCurrentSession().update(item);
        return true;
    }

    @Override
    public void eliminar(Long id) {
        Ancho presentacion = (Ancho) sessionFactory.getCurrentSession().createCriteria(Ancho.class)
                .add(Restrictions.eq("id", id))
                .uniqueResult();

        if (presentacion != null) {
            sessionFactory.getCurrentSession().remove(presentacion);
        }
    }

    public Ancho buscarPorNombre(String ancho) {

        return (Ancho) sessionFactory.getCurrentSession()
                .createCriteria(Ancho.class)
                .add(Restrictions.eq("nombre", ancho))
                .uniqueResult();
    }



}

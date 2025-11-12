package com.tallerwebi.infraestructura;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.tallerwebi.dominio.entidades.Ancho;
import com.tallerwebi.dominio.entidades.Marca;
import com.tallerwebi.dominio.repositorios_interfaces.RepositorioGenerico;

@Repository("repositorioMarca")
public class RepositorioMarcaImpl implements RepositorioGenerico<Marca> {
    // private final Map<Long, Producto> database;
    // private static Long proximoId;
    private SessionFactory sessionFactory;

    @Autowired
    public RepositorioMarcaImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Marca obtener(Long id) {
        final Session session = sessionFactory.getCurrentSession();
        return (Marca) session.createCriteria(Marca.class)
                .add(Restrictions.eq("id", id))
                .uniqueResult();
    }

    @Override
    public List<Marca> obtener() {
        return sessionFactory.getCurrentSession()
                .createQuery("from Marca", Marca.class)
                .list();
    }

    @Override
    public Boolean guardar(Marca item) {
        sessionFactory.getCurrentSession().save(item);
        return true;
    }

    @Override
    public Boolean actualizar(Marca item) {
        sessionFactory.getCurrentSession().update(item);
        return true;
    }

    @Override
    public void eliminar(Long id) {
        Marca presentacion = (Marca) sessionFactory.getCurrentSession().createCriteria(Marca.class)
                .add(Restrictions.eq("id", id))
                .uniqueResult();

        if (presentacion != null) {
            sessionFactory.getCurrentSession().remove(presentacion);
        }
    }

    public Marca buscarPorNombre(String marca) {
        return (Marca) sessionFactory.getCurrentSession()
                .createCriteria(Marca.class)
                .add(Restrictions.eq("nombre", marca))
                .uniqueResult();
    }
}

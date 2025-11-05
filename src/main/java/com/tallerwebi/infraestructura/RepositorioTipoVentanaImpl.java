package com.tallerwebi.infraestructura;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.tallerwebi.dominio.entidades.TipoVentana;
import com.tallerwebi.dominio.repositorios_interfaces.RepositorioGenerico;

@Repository("repositorioTipoVentana")
public class RepositorioTipoVentanaImpl implements RepositorioGenerico<TipoVentana> {
    // private final Map<Long, Producto> database;
    // private static Long proximoId;
    private SessionFactory sessionFactory;

    @Autowired
    public RepositorioTipoVentanaImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public TipoVentana obtener(Long id) {
        final Session session = sessionFactory.getCurrentSession();
        return (TipoVentana) session.createCriteria(TipoVentana.class)
                .add(Restrictions.eq("id", id))
                .uniqueResult();
    }

    @Override
    public List<TipoVentana> obtener() {
        return sessionFactory.getCurrentSession()
                .createQuery("from TipoVentana", TipoVentana.class)
                .list();
    }

    public List<TipoVentana> obtenerPorIdTipoProducto(Long id_tipo_producto) {
        return sessionFactory.getCurrentSession()
                .createQuery("from TipoVentana where tipo_producto_id = :tipo_producto_id", TipoVentana.class)
                .setParameter("tipo_producto_id", id_tipo_producto)
                .list();
    }

    @Override
    public Boolean guardar(TipoVentana item) {
        sessionFactory.getCurrentSession().save(item);
        return true;
    }

    @Override
    public Boolean actualizar(TipoVentana item) {
        sessionFactory.getCurrentSession().update(item);
        return true;
    }

    @Override
    public void eliminar(Long id) {
        TipoVentana presentacion = (TipoVentana) sessionFactory.getCurrentSession().createCriteria(TipoVentana.class)
                .add(Restrictions.eq("id", id))
                .uniqueResult();

        if (presentacion != null) {
            sessionFactory.getCurrentSession().remove(presentacion);
        }
    }
}

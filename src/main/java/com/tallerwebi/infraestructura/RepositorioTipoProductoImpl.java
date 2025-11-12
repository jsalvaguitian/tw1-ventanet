package com.tallerwebi.infraestructura;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.tallerwebi.dominio.entidades.TipoDeVidrio;
import com.tallerwebi.dominio.entidades.TipoProducto;
import com.tallerwebi.dominio.repositorios_interfaces.RepositorioGenerico;

@Repository("repositorioTipoProducto")
public class RepositorioTipoProductoImpl implements RepositorioGenerico<TipoProducto> {
    // private final Map<Long, Producto> database;
    // private static Long proximoId;
    private SessionFactory sessionFactory;

    @Autowired
    public RepositorioTipoProductoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public TipoProducto obtener(Long id) {
        final Session session = sessionFactory.getCurrentSession();
        return (TipoProducto) session.createCriteria(TipoProducto.class)
                .add(Restrictions.eq("id", id))
                .uniqueResult();
    }

    @Override
    public List<TipoProducto> obtener() {
        return sessionFactory.getCurrentSession()
                .createQuery("from TipoProducto", TipoProducto.class)
                .list();
    }

    @Override
    public Boolean guardar(TipoProducto item) {
        sessionFactory.getCurrentSession().save(item);
        return true;
    }

    @Override
    public Boolean actualizar(TipoProducto item) {
        sessionFactory.getCurrentSession().update(item);
        return true;
    }

    @Override
    public void eliminar(Long id) {
        TipoProducto presentacion = (TipoProducto) sessionFactory.getCurrentSession().createCriteria(TipoProducto.class)
                .add(Restrictions.eq("id", id))
                .uniqueResult();

        if (presentacion != null) {
            sessionFactory.getCurrentSession().remove(presentacion);
        }
    }

    public TipoProducto buscarPorNombre(String tipoProducto) {
        return (TipoProducto) sessionFactory.getCurrentSession()
                .createCriteria(TipoProducto.class)
                .add(Restrictions.eq("nombre", tipoProducto))
                .uniqueResult();       
    }
}

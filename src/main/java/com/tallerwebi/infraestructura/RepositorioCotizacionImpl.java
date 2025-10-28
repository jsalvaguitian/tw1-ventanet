package com.tallerwebi.infraestructura;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.tallerwebi.dominio.entidades.Cotizacion;
import com.tallerwebi.dominio.repositorios_interfaces.RepositorioGenerico;

@Repository("repositorioCotizacion")
public class RepositorioCotizacionImpl implements RepositorioGenerico<Cotizacion> {
    // private final Map<Long, Producto> database;
    // private static Long proximoId;
    private SessionFactory sessionFactory;

    @Autowired
    public RepositorioCotizacionImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Cotizacion obtener(Long id) {
        final Session session = sessionFactory.getCurrentSession();
        return (Cotizacion) session.createCriteria(Cotizacion.class)
                .add(Restrictions.eq("clienteId", id))
                .uniqueResult();
    }

    @Override
    public List<Cotizacion> obtener() {
        return sessionFactory.getCurrentSession()
                .createQuery("from Cotizacion", Cotizacion.class)
                .list();
    }

    @Override
    public void eliminar(Long id) {
        Cotizacion cotizacion = (Cotizacion) sessionFactory.getCurrentSession().createCriteria(Cotizacion.class)
                .add(Restrictions.eq("id", id))
                .uniqueResult();

        if (cotizacion != null) {
            sessionFactory.getCurrentSession().remove(cotizacion);
        }
    }

    @Override
    public Boolean guardar(Cotizacion item) {
        sessionFactory.getCurrentSession().save(item);
        return true;
    }

    @Override
    public Boolean actualizar(Cotizacion item) {
        sessionFactory.getCurrentSession().update(item);
        return true;
    }

    public List<Cotizacion> obtenerPorIdCliente(Long clienteId) {
       return sessionFactory.getCurrentSession()
            .createQuery("from Cotizacion c where c.clienteId = :clienteId", Cotizacion.class)
            .setParameter("clienteId", clienteId)
            .list();
    }
}

package com.tallerwebi.infraestructura;

import java.util.List;

import javax.persistence.criteria.JoinType;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.tallerwebi.dominio.entidades.Cotizacion;
import com.tallerwebi.dominio.repositorios_interfaces.RepositorioCotizacion;

@Repository("repositorioCotizacion")
public class RepositorioCotizacionImpl implements RepositorioCotizacion {

    private SessionFactory sessionFactory;

    @Autowired
    public RepositorioCotizacionImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Cotizacion obtenerPorId(Long id) {
        final Session session = sessionFactory.getCurrentSession();

        var query = session.createQuery(
                "SELECT c FROM Cotizacion c " +
                        "JOIN FETCH c.cliente " +
                        "JOIN FETCH c.proveedor " +
                        "LEFT JOIN FETCH c.items i " +
                        "LEFT JOIN FETCH i.producto " +
                        "WHERE c.id = :id",
                Cotizacion.class);
        query.setParameter("id", id);
        return query.uniqueResult();
    }

    @Override
    public List<Cotizacion> obtenerPorIdProveedor(Long proveedorId) {
        var session = sessionFactory.getCurrentSession();
        var query = session.createQuery(
                "SELECT c FROM Cotizacion c " +
                        "JOIN FETCH c.cliente " +
                        "JOIN FETCH c.proveedor " +
                        "WHERE c.proveedor.id = :proveedorId",
                Cotizacion.class);
        query.setParameter("proveedorId", proveedorId);
        return query.getResultList();
    }

    @Override
    public List<Cotizacion> obtenerPorIdProveedorYEstado(Long proveedorId, String estado) {
        var session = sessionFactory.getCurrentSession();
        var cb = session.getCriteriaBuilder();
        var query = cb.createQuery(Cotizacion.class);
        var root = query.from(Cotizacion.class);

        query.select(root).where(
                cb.and(
                        cb.equal(root.get("proveedor").get("id"), proveedorId),
                        cb.equal(root.get("estado").get("estado"), estado)));

        return session.createQuery(query).getResultList();
    }

    @Override
    public boolean actualizarEstado(Cotizacion item) {
        sessionFactory.getCurrentSession().update(item);
        return true;
    }

    /*
     * public List<Cotizacion> obtenerPorIdCliente(Long clienteId) {
     * return sessionFactory.getCurrentSession()
     * .createQuery("from Cotizacion c where c.cliente.id = :clienteId",
     * Cotizacion.class)
     * .setParameter("clienteId", clienteId)
     * .list();
     * }
     */
    @Override
    public List<Cotizacion> obtenerPorIdCliente(Long clienteId) {
        var session = sessionFactory.getCurrentSession();
        var query = session.createQuery(
            "SELECT DISTINCT c FROM Cotizacion c " +
            "JOIN FETCH c.cliente " +
            "JOIN FETCH c.proveedor " +
            "LEFT JOIN FETCH c.items i " +
            "LEFT JOIN FETCH i.producto " +
            "WHERE c.cliente.id = :clienteId " +
            "ORDER BY c.fechaCreacion DESC",
            Cotizacion.class);
        query.setParameter("clienteId", clienteId);
        return query.getResultList();
    }

}


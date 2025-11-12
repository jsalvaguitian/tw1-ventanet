package com.tallerwebi.infraestructura;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.tallerwebi.dominio.entidades.Licitacion;
import com.tallerwebi.dominio.repositorios_interfaces.RepositorioLicitacion;

@Repository("repositorioLicitacion")
public class RepositorioLicitacionImpl implements RepositorioLicitacion{

    private SessionFactory sessionFactory;

    @Autowired
    public RepositorioLicitacionImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Licitacion guardar(Licitacion licitacion) {
        final Session session = sessionFactory.getCurrentSession();
        session.save(licitacion);
        return licitacion;
    }

    @Override
    public Licitacion obtenerPorId(Long id) {
        final Session session = sessionFactory.getCurrentSession();

        var query = session.createQuery(
                "SELECT c FROM Licitacion c " +
                        "JOIN FETCH c.cliente " +
                        "JOIN FETCH c.proveedor " +
                        "JOIN FETCH c.productoCustom i " +                        
                        "WHERE c.id = :id",
                Licitacion.class);
        query.setParameter("id", id);
        return query.uniqueResult();
    }

    @Override
    public List<Licitacion> obtenerPorIdProveedor(Long proveedorId) {
       var session = sessionFactory.getCurrentSession();
        var query = session.createQuery(
                "SELECT c FROM Licitacion c " +
                        "JOIN FETCH c.cliente " +
                        "JOIN FETCH c.proveedor " +
                        "JOIN FETCH c.productoCustom i " +  
                        "WHERE c.proveedor.id = :proveedorId",
                Licitacion.class);
        query.setParameter("proveedorId", proveedorId);
        return query.getResultList();
    }

    @Override
    public List<Licitacion> obtenerPorIdProveedorYEstado(Long proveedorId, String estado) {
        var session = sessionFactory.getCurrentSession();
        var cb = session.getCriteriaBuilder();
        var query = cb.createQuery(Licitacion.class);
        var root = query.from(Licitacion.class);

        query.select(root).where(
                cb.and(
                        cb.equal(root.get("proveedor").get("id"), proveedorId),
                        cb.equal(root.get("estado").get("estado"), estado)));

        return session.createQuery(query).getResultList();
    }

    @Override
    public boolean actualizarEstado(Licitacion item) {
        sessionFactory.getCurrentSession().update(item);
        return true;
    }

    @Override
    public List<Licitacion> obtenerPorIdCliente(Long id) {
        var session = sessionFactory.getCurrentSession();
        var query = session.createQuery(
            "SELECT DISTINCT c FROM Licitacion c " +
            "JOIN FETCH c.cliente " +
            "JOIN FETCH c.proveedor " +
            "JOIN JOIN FETCH c.productoCustom i " +            
            "WHERE c.cliente.id = :clienteId " +
            "ORDER BY c.fechaCreacion DESC",
            Licitacion.class);
        query.setParameter("clienteId", id);
        return query.getResultList();
    }
}

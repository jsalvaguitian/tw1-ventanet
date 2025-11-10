package com.tallerwebi.infraestructura;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.tallerwebi.dominio.entidades.Comentario;
import com.tallerwebi.dominio.repositorios_interfaces.RepositorioComentario;


@Repository("repositorioComentario")
public class RepositorioComentarioImpl implements RepositorioComentario {
    private SessionFactory sessionFactory;

    @Autowired
    public RepositorioComentarioImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Comentario obtenerPorId(Long id) {
        final Session session = sessionFactory.getCurrentSession();
        var query = session.createQuery(
                "SELECT c FROM Comentario c " +
                        "LEFT JOIN FETCH c.cliente " +
                        "LEFT JOIN FETCH c.proveedor " +
                        "JOIN FETCH c.cotizacion " +
                        "WHERE c.id = :id",
                Comentario.class);
        query.setParameter("id", id);
        return query.uniqueResult();
    }

    @Override
    public List<Comentario> obtenerPorIdCotizacion(Long cotizacionId) {
        var session = sessionFactory.getCurrentSession();
        var query = session.createQuery(
                "SELECT c FROM Comentario c " +
                        "LEFT JOIN FETCH c.cliente " +
                        "LEFT JOIN FETCH c.proveedor " +
                        "WHERE c.cotizacion.id = :cotizacionId " +
                        "ORDER BY c.fechaCreacion ASC",
                Comentario.class);
        query.setParameter("cotizacionId", cotizacionId);
        return query.getResultList();
    }

    @Override
    public Comentario guardar(Comentario comentario) {
        sessionFactory.getCurrentSession().save(comentario);
        return comentario;
    }

    @Override
    public void actualizar(Comentario comentario) {
        sessionFactory.getCurrentSession().update(comentario);
    }

    @Override
    public List<Comentario> listarTodos() {
        var session = sessionFactory.getCurrentSession();
        var query = session.createQuery(
                "SELECT c FROM Comentario c " +
                        "LEFT JOIN FETCH c.cliente " +
                        "LEFT JOIN FETCH c.proveedor " +
                        "JOIN FETCH c.cotizacion " +
                        "ORDER BY c.fechaCreacion ASC",
                Comentario.class);
        return query.getResultList();
    }

    @Override
    public long contarNoLeidosParaCotizacionCliente(Long cotizacionId) {
    var session = sessionFactory.getCurrentSession();
    var query = session.createQuery(
        "SELECT COUNT(c) FROM Comentario c WHERE c.cotizacion.id = :cid AND c.leidoPorCliente = false",
        Long.class);
    query.setParameter("cid", cotizacionId);
    Long result = query.uniqueResult();
    return result == null ? 0L : result;
    }

    @Override
    public long contarNoLeidosParaCotizacionProveedor(Long cotizacionId) {
    var session = sessionFactory.getCurrentSession();
    var query = session.createQuery(
        "SELECT COUNT(c) FROM Comentario c WHERE c.cotizacion.id = :cid AND c.leidoPorProveedor = false",
        Long.class);
    query.setParameter("cid", cotizacionId);
    Long result = query.uniqueResult();
    return result == null ? 0L : result;
    }

}

package com.tallerwebi.infraestructura;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.tallerwebi.dominio.entidades.Cotizacion;
import com.tallerwebi.dominio.entidades.CotizacionItem;
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

    @Override
    public Cotizacion guardar(Cotizacion cotizacion) {
        final Session session = sessionFactory.getCurrentSession();
        session.save(cotizacion);
        return cotizacion;
    }

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

    @Override
    public Long contarCotizacionesAprobadasPorProveedor(Long proveedorId) {
        var session = sessionFactory.getCurrentSession();
        var query = session.createQuery(
                "SELECT COUNT(c) FROM Cotizacion c WHERE c.proveedor.id = :proveedorId AND c.estado = 'APROBADA'",
                Long.class);
        query.setParameter("proveedorId", proveedorId);
        return query.uniqueResult();

    }

    @Override
    public Long contarCotizacionesRechazadasPorProveedor(Long proveedorId) {
        var session = sessionFactory.getCurrentSession();
        var query = session.createQuery(
                "SELECT COUNT(c) FROM Cotizacion c WHERE c.proveedor.id = :proveedorId AND c.estado = 'RECHAZADO'",
                Long.class);
        query.setParameter("proveedorId", proveedorId);
        return query.uniqueResult();
    }

    @Override
    public Long contarCotizacionesCompletadasPorProveedor(Long proveedorId) {
        var session = sessionFactory.getCurrentSession();
        var query = session.createQuery(
                "SELECT COUNT(c) FROM Cotizacion c WHERE c.proveedor.id = :proveedorId AND c.estado = 'COMPLETADA'",
                Long.class);
        query.setParameter("proveedorId", proveedorId);
        return query.uniqueResult();
    }

    @Override
    public Long contarCotizacionesPendientesPorProveedor(Long proveedorId) {
        var session = sessionFactory.getCurrentSession();
        var query = session.createQuery(
                "SELECT COUNT(c) FROM Cotizacion c WHERE c.proveedor.id = :proveedorId AND c.estado = 'PENDIENTE'",
                Long.class);
        query.setParameter("proveedorId", proveedorId);
        return query.uniqueResult();
    }
    /*
     * public Long promedioDeCotizacionesCompletadasPorProveedor(Long proveedorId) {
     * var session = sessionFactory.getCurrentSession();
     * var query = session.createQuery(
     * "SELECT COUNT(c) FROM Cotizacion c " +
     * "WHERE c.proveedor.id = :proveedorId AND c.estado = 'COMPLETADA'",
     * Long.class);
     * query.setParameter("proveedorId", proveedorId);
     * return query.uniqueResult();
     * }
     */

    @Override // Me tira error sql saco el promedio sin sql -->
    public Double obtenerPromedioGeneralCompletadas() {
        var session = sessionFactory.getCurrentSession();
        var query = session.createQuery(
                "SELECT c.proveedor.id, COUNT(c) FROM Cotizacion c " +
                        "WHERE c.estado = 'COMPLETADA' GROUP BY c.proveedor.id",
                Object[].class); // Esto es porque el select de arriba devuelve una TUPLA que no es una entidad
                                 // ni un numero

        List<Object[]> resultados = query.getResultList();
        if (resultados.isEmpty()) {
            return 0.0;
        }

        double total = 0.0;
        for (Object[] fila : resultados) {
            Long count = (Long) fila[1];
            total += count;
        }

        return total / resultados.size();
    }

    @Override
    public List<Object[]> obtenerProductosMasCotizados(Long proveedorId) {
        var session = sessionFactory.getCurrentSession();
        var query = session.createQuery(
                "SELECT ci.producto.nombre, COUNT(DISTINCT ci.cotizacion.id) " +
                        "FROM CotizacionItem ci " +
                        "JOIN ci.cotizacion c " +
                        "WHERE c.proveedor.id = :proveedorId " +
                        "AND c.estado = 'APROBADA' " +
                        "GROUP BY ci.producto.nombre " +
                        "ORDER BY COUNT(DISTINCT ci.cotizacion.id) DESC",
                Object[].class);
        query.setParameter("proveedorId", proveedorId);
        return query.getResultList();
    }

    @Override
    public List<Object[]> obtenerProductosMasCotizadosDeTodosLosProveedores() {
        var session = sessionFactory.getCurrentSession();
        var query = session.createQuery(
                "SELECT p.nombre, SUM(i.cantidad) " +
                        "FROM Cotizacion c " +
                        "JOIN c.items i " +
                        "JOIN i.producto p " +
                        "WHERE c.estado = com.tallerwebi.dominio.enums.EstadoCotizacion.APROBADA " +
                        "GROUP BY p.nombre " +
                        "ORDER BY SUM(i.cantidad) DESC",
                Object[].class);
        return query.getResultList();

    }

}

package com.tallerwebi.infraestructura;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.tallerwebi.dominio.entidades.Presupuesto;
import com.tallerwebi.dominio.repositorios_interfaces.RepositorioPresupuesto;

@Repository("repositorioPresupuesto")
public class RepositorioPresupuestoImpl implements RepositorioPresupuesto {

    private final SessionFactory sessionFactory;

    @Autowired
    public RepositorioPresupuestoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Presupuesto guardar(Presupuesto presupuesto) {
        final Session session = sessionFactory.getCurrentSession();
        session.save(presupuesto);
        return presupuesto;
    }

        public List<Presupuesto> obtenerPorIdCliente(Long clienteId) {
            Session session = sessionFactory.getCurrentSession();
            // HQL: select by entity properties, not by table/column names
            var query = session.createQuery(
                "SELECT DISTINCT p " +
                "FROM Presupuesto p " +
                "JOIN FETCH p.provincia " +
                "JOIN FETCH p.localidad " +
                "JOIN FETCH p.partido " +
                "WHERE p.cliente.id = :clienteId " +
                "ORDER BY p.fechaCreacion DESC",
                Presupuesto.class);
            query.setParameter("clienteId", clienteId);
            return query.getResultList();
        }
    
        @Override
        public Presupuesto obtenerPorId(Long id) {
        Session session = sessionFactory.getCurrentSession();
        // Eagerly fetch related entities used by the view (items and location
        // references) to avoid LazyInitializationException when rendering outside
        // the transactional session
        var q = session.createQuery(
            "SELECT DISTINCT p FROM Presupuesto p "
                + "LEFT JOIN FETCH p.items it "
                + "LEFT JOIN FETCH it.tipoProducto tp "
                + "LEFT JOIN FETCH it.tipoVentana tv "
                + "LEFT JOIN FETCH it.ancho a "
                + "LEFT JOIN FETCH it.alto al "
                + "LEFT JOIN FETCH it.material m "
                + "LEFT JOIN FETCH it.color c "
                + "LEFT JOIN FETCH it.usuario u "
                + "LEFT JOIN FETCH p.provincia pr "
                + "LEFT JOIN FETCH p.localidad lo "
                + "LEFT JOIN FETCH p.partido pa "
                + "WHERE p.id = :id",
            Presupuesto.class);
        q.setParameter("id", id);
        return (Presupuesto) q.uniqueResult();
        }
}

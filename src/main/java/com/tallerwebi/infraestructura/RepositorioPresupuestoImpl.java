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
}

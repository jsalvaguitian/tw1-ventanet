package com.tallerwebi.infraestructura;

import java.util.List;

import javax.persistence.Query;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.tallerwebi.dominio.entidades.Proveedor;
import com.tallerwebi.dominio.enums.EstadoUsuario;
import com.tallerwebi.dominio.enums.Rubro;
import com.tallerwebi.dominio.repositorios_interfaces.RepositorioProveedor;

@Repository("repositorioProveedor")
public class RepositorioProveedorImpl implements RepositorioProveedor {

    private SessionFactory sessionFactory;

    @Autowired
    public RepositorioProveedorImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Proveedor buscarProveedorPorCuit(String cuit) {

        return (Proveedor) sessionFactory.getCurrentSession()
                .createCriteria(Proveedor.class)
                .add(Restrictions.eq("cuit", cuit))
                .uniqueResult();
    }

    @Override
    public List<Proveedor> obtenerTodosLosNombresProveedoresActivos() {

        String hql = "FROM Proveedor p WHERE p.estado = :estado";
        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("estado", EstadoUsuario.ACTIVO);

        return query.getResultList();
    }

    /*
     * @Override
     * public Proveedor buscarProveedorPorIdUsuario(Long idUsuario) {
     * return (Proveedor) sessionFactory.getCurrentSession()
     * .createCriteria(Proveedor.class)
     * .add(Restrictions.eq("id", idUsuario) )
     * .uniqueResult();
     * }
     */

    @Override
    public List<Proveedor> obtenerTodosLosProveedoresPendientes() {
        String hql = "FROM Proveedor p WHERE p.estado = :estado";
        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("estado", EstadoUsuario.PENDIENTE);

        return query.getResultList();
    }

    @Override
    public Proveedor buscarPorId(Long id) {
        return (Proveedor) sessionFactory.getCurrentSession()
                .createCriteria(Proveedor.class)
                .add(Restrictions.eq("id", id))
                .uniqueResult();
    }

    @Override
    public void actualizar(Proveedor proveedor) {
        sessionFactory.getCurrentSession().update(proveedor);
    }

    @Override
    public Proveedor buscarProveedorPorIdUsuario(Long idUsuario) {
        return (Proveedor) sessionFactory.getCurrentSession()
                .createCriteria(Proveedor.class)
                .add(Restrictions.eq("id", idUsuario))
                .uniqueResult();
    }

    @Override
    public Integer contarProveedores() {
        String hql = "SELECT COUNT(p.id) FROM Proveedor p";
        Long count = (Long) sessionFactory.getCurrentSession().createQuery(hql).uniqueResult();
        return count.intValue();
    }

    @Override
    public Integer contarProveedores(EstadoUsuario estado) {
        String hql = "SELECT COUNT(p.id) FROM Proveedor p WHERE p.estado = :estado";
        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("estado", estado);
        Long count = (Long) query.getSingleResult();
        return count.intValue();
    }

    @Override
    public List<Rubro> obtenerRubrosActivos() {
        String hql = "SELECT DISTINCT p.rubro FROM Proveedor p WHERE p.activo = true";
        org.hibernate.query.Query<Rubro> query = this.sessionFactory.getCurrentSession().createQuery(hql, Rubro.class);
        return query.getResultList();

    }

    @Override
    public List<Proveedor> listarPorRubro(Rubro rubro) {

        String hql = "FROM Proveedor p where p.rubro =: rubro AND p.activo = TRUE";
        org.hibernate.query.Query<Proveedor> query = this.sessionFactory.getCurrentSession().createQuery(hql,
                Proveedor.class);
        query.setParameter("rubro", rubro);

        return query.getResultList();
    }

    @Override
    public List<Proveedor> listarTodosPorEstado(Boolean activo) {

        String hql = "FROM Proveedor p where p.activo =: activo";
        org.hibernate.query.Query<Proveedor> query = this.sessionFactory.getCurrentSession().createQuery(hql,
                Proveedor.class);
        query.setParameter("activo", activo);

        return query.getResultList();
    }

    @Override
    public Proveedor obtenerProveedorConMedios(Long id) {

        String hql = "SELECT p FROM Proveedor p JOIN FETCH p.mediosDePago WHERE p.id = :id";
        sessionFactory.getCurrentSession()
                .createQuery(hql, Proveedor.class);
        org.hibernate.query.Query<Proveedor> query = this.sessionFactory.getCurrentSession().createQuery(hql,
                Proveedor.class);
        query.setParameter("id", id);
        return query.uniqueResult();

    }

}

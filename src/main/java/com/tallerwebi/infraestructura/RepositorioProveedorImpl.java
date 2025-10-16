package com.tallerwebi.infraestructura;

import java.util.List;

import javax.persistence.Query;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.tallerwebi.dominio.entidades.Proveedor;
import com.tallerwebi.dominio.enums.EstadoProveedor;
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
        query.setParameter("estado", EstadoProveedor.ACTIVO);
        
        return query.getResultList();

    @Override
    public Proveedor buscarProveedorPorIdUsuario(Long idUsuario) {
        return (Proveedor) sessionFactory.getCurrentSession()
                .createCriteria(Proveedor.class)
                .add(Restrictions.eq("id", idUsuario) )
                .uniqueResult();
    }

}

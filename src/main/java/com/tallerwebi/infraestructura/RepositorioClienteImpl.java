package com.tallerwebi.infraestructura;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.tallerwebi.dominio.entidades.Cliente;
import com.tallerwebi.dominio.repositorios_interfaces.RepositorioCliente;

@Repository("repositorioCliente")
public class RepositorioClienteImpl implements RepositorioCliente {
    private SessionFactory sessionFactory;

    @Autowired
    public RepositorioClienteImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;  
    }

    @Override
    public Integer contarClientes() {
        String hql = "SELECT COUNT(c) FROM Cliente c";
        Long count = (Long) this.sessionFactory.getCurrentSession().createQuery(hql).uniqueResult();
        return count.intValue();
    }

    @Override
    public Cliente buscarPorId(Long id) {
        return (Cliente) sessionFactory.getCurrentSession().createCriteria(Cliente.class)
                .add(Restrictions.eq("id", id))
                .uniqueResult();
    }

}

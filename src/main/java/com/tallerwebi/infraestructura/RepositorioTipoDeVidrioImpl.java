package com.tallerwebi.infraestructura;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.tallerwebi.dominio.entidades.TipoDeVidrio;
import com.tallerwebi.dominio.repositorios_interfaces.RepositorioTipoDeVidrio;

@Repository("repositorioTipoDeVidrio")
public class RepositorioTipoDeVidrioImpl implements RepositorioTipoDeVidrio {

    private SessionFactory sessionFactory;

    @Autowired
    public RepositorioTipoDeVidrioImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public TipoDeVidrio buscarPorId(Long id) {
        final Session session = sessionFactory.getCurrentSession();
        return (TipoDeVidrio) session.createCriteria(TipoDeVidrio.class)
                .add(Restrictions.eq("id", id))
                .uniqueResult();
    }

}

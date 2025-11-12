package com.tallerwebi.infraestructura;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.tallerwebi.dominio.entidades.SubTipoProducto;
import com.tallerwebi.dominio.entidades.TipoDeVidrio;
import com.tallerwebi.dominio.repositorios_interfaces.RepositorioTipoDeVidrio;

@Repository
public class RepositorioTipoDeVidrioImpl implements RepositorioTipoDeVidrio{
    
    private SessionFactory sessionFactory;

    

    @Autowired
    public RepositorioTipoDeVidrioImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }



    @Override
    public TipoDeVidrio buscarPorNombre(String tipoVidrio) {
        return (TipoDeVidrio) sessionFactory.getCurrentSession()
                .createCriteria(TipoDeVidrio.class)
                .add(Restrictions.eq("nombre", tipoVidrio))
                .uniqueResult();
    }



    @Override
    public void guardar(TipoDeVidrio item) {
        sessionFactory.getCurrentSession().save(item);
    }



    @Override
    public List<TipoDeVidrio> obtener() {
        return sessionFactory.getCurrentSession()
                .createQuery("from TipoDeVidrio", TipoDeVidrio.class)
                .list();
    }

}

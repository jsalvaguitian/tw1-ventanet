package com.tallerwebi.dominio.repositorios_interfaces;

import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import org.hibernate.SessionFactory;
import com.tallerwebi.dominio.entidades.ResetearPasswordToken;

@Repository
public class RepositorioTokenRecuperarPasswordImpl implements RepositorioTokenRecuperarPassword {

    private SessionFactory sessionFactory;

    @Autowired
    public RepositorioTokenRecuperarPasswordImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void eliminar(ResetearPasswordToken tokenRecibido) {
        sessionFactory.getCurrentSession().delete(tokenRecibido);

    }

    @Override
    public void guardar(ResetearPasswordToken tokenEntidad) {
        sessionFactory.getCurrentSession().save(tokenEntidad);
    }

    @Override
    public ResetearPasswordToken buscarPorToken(String token) {
        return (ResetearPasswordToken) sessionFactory.getCurrentSession().createCriteria(ResetearPasswordToken.class)
                .add(Restrictions.eq("token", token))
                .uniqueResult();

    }
}

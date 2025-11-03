package com.tallerwebi.infraestructura;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.tallerwebi.dominio.entidades.MaterialDePerfil;
import com.tallerwebi.dominio.repositorios_interfaces.RepositorioGenerico;

@Repository("repositorioMaterial")
public class RepositorioMaterialImpl implements RepositorioGenerico<MaterialDePerfil> {
    // private final Map<Long, Producto> database;
    // private static Long proximoId;
    private SessionFactory sessionFactory;

    @Autowired
    public RepositorioMaterialImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<MaterialDePerfil> obtener() {
        return sessionFactory.getCurrentSession()
                .createQuery("from MaterialDePerfil", MaterialDePerfil.class)
                .list();
    }

    @Override
    public MaterialDePerfil obtener(Long id) {
        final Session session = sessionFactory.getCurrentSession();
        return (MaterialDePerfil) session.createCriteria(MaterialDePerfil.class)
                .add(Restrictions.eq("id", id))
                .uniqueResult();
    }

    @Override
    public Boolean guardar(MaterialDePerfil item) {
        sessionFactory.getCurrentSession().save(item);
        return true;
    }

    @Override
    public Boolean actualizar(MaterialDePerfil item) {
        sessionFactory.getCurrentSession().update(item);
        return true;
    }

    @Override
    public void eliminar(Long id) {
        MaterialDePerfil presentacion = (MaterialDePerfil) sessionFactory.getCurrentSession().createCriteria(MaterialDePerfil.class)
                .add(Restrictions.eq("id", id))
                .uniqueResult();

        if (presentacion != null) {
            sessionFactory.getCurrentSession().remove(presentacion);
        }
    }

}

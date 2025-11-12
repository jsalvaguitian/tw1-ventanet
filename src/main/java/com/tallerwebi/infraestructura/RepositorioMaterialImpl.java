package com.tallerwebi.infraestructura;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.tallerwebi.dominio.entidades.Ancho;
import com.tallerwebi.dominio.entidades.Material;
import com.tallerwebi.dominio.repositorios_interfaces.RepositorioGenerico;

@Repository("repositorioMaterial")
public class RepositorioMaterialImpl implements RepositorioGenerico<Material> {
    // private final Map<Long, Producto> database;
    // private static Long proximoId;
    private SessionFactory sessionFactory;

    @Autowired
    public RepositorioMaterialImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Material> obtener() {
        return sessionFactory.getCurrentSession()
                .createQuery("from Material", Material.class)
                .list();
    }

    @Override
    public Material obtener(Long id) {
        final Session session = sessionFactory.getCurrentSession();
        return (Material) session.createCriteria(Material.class)
                .add(Restrictions.eq("id", id))
                .uniqueResult();
    }

    @Override
    public Boolean guardar(Material item) {
        sessionFactory.getCurrentSession().save(item);
        return true;
    }

    @Override
    public Boolean actualizar(Material item) {
        sessionFactory.getCurrentSession().update(item);
        return true;
    }

    @Override
    public void eliminar(Long id) {
        Material presentacion = (Material) sessionFactory.getCurrentSession().createCriteria(Material.class)
                .add(Restrictions.eq("id", id))
                .uniqueResult();

        if (presentacion != null) {
            sessionFactory.getCurrentSession().remove(presentacion);
        }
    }

    public Material buscarPorNombre(String material) {
        return (Material) sessionFactory.getCurrentSession()
                .createCriteria(Material.class)
                .add(Restrictions.eq("nombre", material))
                .uniqueResult();
    }

}

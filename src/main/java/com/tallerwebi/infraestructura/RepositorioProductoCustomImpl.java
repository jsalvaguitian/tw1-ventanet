package com.tallerwebi.infraestructura;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.tallerwebi.dominio.entidades.Producto;
import com.tallerwebi.dominio.entidades.ProductoCustom;
import com.tallerwebi.dominio.repositorios_interfaces.RepositorioGenerico;

@Repository("repositorioProductoCustom")
public class RepositorioProductoCustomImpl implements RepositorioGenerico<ProductoCustom> {

    private SessionFactory sessionFactory;

    @Autowired
    public RepositorioProductoCustomImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public ProductoCustom obtener(Long id) {
        final Session session = sessionFactory.getCurrentSession();
        return (ProductoCustom) session.createCriteria(ProductoCustom.class)
                .add(Restrictions.eq("id", id))
                .uniqueResult();
    }

    @Override
    public List<ProductoCustom> obtener() {
        return sessionFactory.getCurrentSession()
                .createQuery("from ProductoCustom", ProductoCustom.class)
                .list();
    }

    @Override
    public Boolean guardar(ProductoCustom item) {
        sessionFactory.getCurrentSession().save(item);
        return true;
    }

    @Override
    public Boolean actualizar(ProductoCustom item) {
       sessionFactory.getCurrentSession().update(item);
        return true;
    }

    @Override
    public void eliminar(Long id) {
        ProductoCustom producto = (ProductoCustom) sessionFactory.getCurrentSession().createCriteria(Producto.class)
                .add(Restrictions.eq("id", id))
                .uniqueResult();

        if (producto != null) {
            sessionFactory.getCurrentSession().remove(producto);
        }
    }

}

package com.tallerwebi.infraestructura;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.tallerwebi.dominio.entidades.Producto;
import com.tallerwebi.dominio.repositorios_interfaces.RepositorioGenerico;

@Repository("repositorioProducto")
public class RepositorioProductoImpl implements RepositorioGenerico<Producto> {
    // private final Map<Long, Producto> database;
    // private static Long proximoId;
    private SessionFactory sessionFactory;

    @Autowired
    public RepositorioProductoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Producto obtener(Long id) {
        final Session session = sessionFactory.getCurrentSession();
        return (Producto) session.createCriteria(Producto.class)
                .add(Restrictions.eq("id", id))
                .uniqueResult();
    }

    @Override
    public List<Producto> obtener() {
        return sessionFactory.getCurrentSession()
                .createQuery("from Producto", Producto.class)
                .list();
    }

    @Override
    public Boolean guardar(Producto item) {
        sessionFactory.getCurrentSession().save(item);
        return true;
    }

    @Override
    public Boolean actualizar(Producto item) {
        sessionFactory.getCurrentSession().update(item);
        return true;
    }

    @Override
    public void eliminar(Long id) {
        Producto producto = (Producto) sessionFactory.getCurrentSession().createCriteria(Producto.class)
                .add(Restrictions.eq("id", id))
                .uniqueResult();

        if (producto != null) {
            sessionFactory.getCurrentSession().remove(producto);
        }
    }
    
    public Producto obtenerPorNombreMarcaYProveedor(String nombre, Long marcaId, Integer proveedorId) {
        var session = sessionFactory.getCurrentSession();
        var cb = session.getCriteriaBuilder();
        var query = cb.createQuery(Producto.class);
        var root = query.from(Producto.class);

        query.select(root).where(
                cb.and(
                        cb.equal(root.get("nombre"), nombre),
                        cb.equal(root.get("marca").get("id"), marcaId),
                        cb.equal(root.get("proveedorId"), proveedorId)));

        return session.createQuery(query).uniqueResult();
    }

}

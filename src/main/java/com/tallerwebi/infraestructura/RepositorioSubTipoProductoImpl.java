package com.tallerwebi.infraestructura;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.tallerwebi.dominio.entidades.SubTipoProducto;
import com.tallerwebi.dominio.entidades.TipoDeVidrio;
import com.tallerwebi.dominio.repositorios_interfaces.RepositorioGenerico;

/* */
@Repository("repositorioTipoVentana")
public class RepositorioSubTipoProductoImpl implements RepositorioGenerico<SubTipoProducto> {
    // private final Map<Long, Producto> database;
    // private static Long proximoId;
    private SessionFactory sessionFactory;

    @Autowired
    public RepositorioSubTipoProductoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public SubTipoProducto obtener(Long id) {
        final Session session = sessionFactory.getCurrentSession();
        return (SubTipoProducto) session.createCriteria(SubTipoProducto.class)
                .add(Restrictions.eq("id", id))
                .uniqueResult();
    }

    @Override
    public List<SubTipoProducto> obtener() {
        return sessionFactory.getCurrentSession()
                .createQuery("from SubTipoProducto", SubTipoProducto.class)
                .list();
    }

    public List<SubTipoProducto> obtenerPorIdTipoProducto(Long idTipoProducto) {
        return sessionFactory.getCurrentSession()
                .createQuery(
                        "select v from SubTipoProducto v join fetch v.tipoProducto tp where tp.id = :idTipoProducto",
                        SubTipoProducto.class)
                .setParameter("idTipoProducto", idTipoProducto)
                .list();
    }

    @Override
    public Boolean guardar(SubTipoProducto item) {
        sessionFactory.getCurrentSession().save(item);
        return true;
    }

    @Override
    public Boolean actualizar(SubTipoProducto item) {
        sessionFactory.getCurrentSession().update(item);
        return true;
    }

    @Override
    public void eliminar(Long id) {
        SubTipoProducto presentacion = (SubTipoProducto) sessionFactory.getCurrentSession()
                .createCriteria(SubTipoProducto.class)
                .add(Restrictions.eq("id", id))
                .uniqueResult();

        if (presentacion != null) {
            sessionFactory.getCurrentSession().remove(presentacion);
        }
    }

    public SubTipoProducto buscarPorNombre(String subtipoProducto) {
        return (SubTipoProducto) sessionFactory.getCurrentSession()
                .createCriteria(SubTipoProducto.class)
                .add(Restrictions.eq("nombre", subtipoProducto))
                .uniqueResult();
    }

    public SubTipoProducto buscarPorNombreYTipo(String nombre, Long tipoProductoId) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SubTipoProducto.class, "sub");
        criteria.createAlias("sub.tipoProducto", "tipo");
        criteria.add(Restrictions.eq("sub.nombre", nombre));
        criteria.add(Restrictions.eq("tipo.id", tipoProductoId));
        return (SubTipoProducto) criteria.uniqueResult();
    }
}

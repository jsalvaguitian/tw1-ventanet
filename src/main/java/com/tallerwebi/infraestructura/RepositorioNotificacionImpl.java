package com.tallerwebi.infraestructura;

import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.tallerwebi.dominio.entidades.Notificacion;
import com.tallerwebi.dominio.repositorios_interfaces.RepositorioNotificacion;

@Repository("repositorioNotificacion")
public class RepositorioNotificacionImpl implements RepositorioNotificacion {

    private final SessionFactory sessionFactory;

    @Autowired
    public RepositorioNotificacionImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Notificacion guardar(Notificacion n) {
        var session = sessionFactory.getCurrentSession();
        session.save(n);
        return n;
    }

    @Override
    public List<Notificacion> obtenerNoLeidasPorUsuario(Long usuarioId, int limit) {
        var session = sessionFactory.getCurrentSession();
        var q = session.createQuery(
                "SELECT n FROM Notificacion n WHERE n.usuario.id = :uid AND n.leida = false ORDER BY n.fechaCreacion DESC",
                Notificacion.class);
        q.setParameter("uid", usuarioId);
        q.setMaxResults(limit);
        return q.getResultList();
    }

    @Override
    public List<Notificacion> obtenerTodasPorUsuario(Long usuarioId, int limit, int offset) {
        var session = sessionFactory.getCurrentSession();
        var q = session.createQuery(
                "SELECT n FROM Notificacion n WHERE n.usuario.id = :uid ORDER BY n.fechaCreacion DESC",
                Notificacion.class);
        q.setParameter("uid", usuarioId);
        q.setFirstResult(offset);
        q.setMaxResults(limit);
        return q.getResultList();
    }

    @Override
    public void marcarComoLeida(Long id) {
        var session = sessionFactory.getCurrentSession();
        var n = session.get(Notificacion.class, id);
        if (n != null && !n.isLeida()) {
            n.setLeida(true);
            session.update(n);
        }
    }

    @Override
    public void marcarTodasComoLeidas(Long usuarioId) {
        var session = sessionFactory.getCurrentSession();
        var q = session
                .createQuery("UPDATE Notificacion n SET n.leida = true WHERE n.usuario.id = :uid AND n.leida = false");
        q.setParameter("uid", usuarioId);
        q.executeUpdate();
    }

    @Override
    public Long contarNoLeidas(Long id) {
        var session = sessionFactory.getCurrentSession();
        var q = session.createQuery(
                "SELECT COUNT(n) FROM Notificacion n WHERE n.usuario.id = :id AND n.leida = false",
                Long.class);
        q.setParameter("id", id);
        var count = (Long) q.uniqueResult();
        return count;
    }
}

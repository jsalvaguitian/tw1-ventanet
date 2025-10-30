package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.repositorios_interfaces.RepositorioUsuario;

import java.util.List;

import javax.persistence.Query;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("repositorioUsuario")
public class RepositorioUsuarioImpl implements RepositorioUsuario {

    private SessionFactory sessionFactory;

    @Autowired
    public RepositorioUsuarioImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Usuario buscarUsuario(String email, String password) {

        final Session session = sessionFactory.getCurrentSession();
        return (Usuario) session.createCriteria(Usuario.class)
                .add(Restrictions.eq("email", email))
                .add(Restrictions.eq("password", password))
                .uniqueResult();
    }

    @Override
    public void guardar(Usuario usuario) {
        sessionFactory.getCurrentSession().save(usuario);
    }

    @Override
    public Usuario buscarPorMail(String email) {
        return (Usuario) sessionFactory.getCurrentSession().createCriteria(Usuario.class)
                .add(Restrictions.eq("email", email))
                .uniqueResult();
    }

    @Override
    public void modificar(Usuario usuario) {
        sessionFactory.getCurrentSession().update(usuario);
    }

    @Override
    public Usuario buscarPorToken(String token) {
        return (Usuario) sessionFactory.getCurrentSession().createCriteria(Usuario.class)
                .add(Restrictions.eq("tokenVerificacion", token))
                .uniqueResult();
    }

    @Override
    public void actualizar(Usuario usuario) {
        sessionFactory.getCurrentSession().update(usuario);
    }

    @Override
    public Integer contarUsuarios() {
        String hql = "SELECT COUNT(u.id) FROM Usuario u WHERE u.rol <> 'ADMIN'";
        Long count = (Long) sessionFactory.getCurrentSession().createQuery(hql).uniqueResult();
        return count.intValue();
    }

    @Override
    public List<Usuario> obtenerTodosLosUsuarios() {
        String hql = "FROM Usuario u WHERE u.rol <> 'ADMIN'";
        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);

        return query.getResultList();
    }

}

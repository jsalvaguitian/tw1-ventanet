package com.tallerwebi.infraestructura;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.tallerwebi.dominio.entidades.CarritoCotizacion;
import com.tallerwebi.dominio.entidades.Cliente;
import com.tallerwebi.dominio.repositorios_interfaces.RepositorioCarritoCoti;

@Repository
public class RepositorioCarritoCotiImpl implements RepositorioCarritoCoti {

    private SessionFactory sessionFactory;

    @Autowired
    public RepositorioCarritoCotiImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public CarritoCotizacion buscarPorClienteYNoConfirmado(Cliente cliente) {

        String hql = "SELECT c FROM CarritoCotizacion c "
                + "LEFT JOIN FETCH c.items "
                + "WHERE c.cliente = :cliente AND c.confirmado = false";

        Query<CarritoCotizacion> query = sessionFactory.getCurrentSession()
                .createQuery(hql, CarritoCotizacion.class);
        query.setParameter("cliente", cliente);

        List<CarritoCotizacion> lista = query.getResultList();
        return lista.isEmpty() ? null : lista.get(0);
    }

    @Override
    public void guardar(CarritoCotizacion carrito) {
        if (carrito.getId() == null)
            sessionFactory.getCurrentSession().save(carrito);
        else
            sessionFactory.getCurrentSession().update(carrito);
    }

}

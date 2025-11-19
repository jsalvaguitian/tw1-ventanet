package com.tallerwebi.infraestructura;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.tallerwebi.presentacion.dto.ProductoMasCotizadoDTO;

@Repository
public class RepositorioEstadisticasImpl implements RepositorioEstadisticas {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<ProductoMasCotizadoDTO> obtenerProductosMasCotizados(int limite) {
        return entityManager.createQuery(
                "SELECT new com.tallerwebi.presentacion.dto.ProductoMasCotizadoDTO( " +
                "   p.id, p.nombre, p.imagenUrl, SUM(ci.cantidad) ) " +
                "FROM CotizacionItem ci " +
                "JOIN ci.producto p " +
                "GROUP BY p.id, p.nombre, p.imagenUrl " +
                "ORDER BY SUM(ci.cantidad) DESC",
                ProductoMasCotizadoDTO.class)
                .setMaxResults(limite)
                .getResultList();
    }
}

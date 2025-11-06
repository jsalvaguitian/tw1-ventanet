package com.tallerwebi.infraestructura;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.tallerwebi.dominio.entidades.Producto;
import com.tallerwebi.dominio.entidades.TipoProducto;
import com.tallerwebi.dominio.entidades.TipoVentana;
import com.tallerwebi.dominio.repositorios_interfaces.RepositorioGenerico;
import com.tallerwebi.presentacion.dto.ProductoDTO;
import com.tallerwebi.presentacion.dto.ProductoGenericoDTO;

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

    public Producto obtenerPorNombreMarcaYProveedor(String nombre, Long marcaId, Long proveedorId) {
        var session = sessionFactory.getCurrentSession();
        var cb = session.getCriteriaBuilder();
        var query = cb.createQuery(Producto.class);
        var root = query.from(Producto.class);

        query.select(root).where(
                cb.and(
                        cb.equal(root.get("nombre"), nombre),
                        cb.equal(root.get("marca").get("id"), marcaId),
                        cb.equal(root.get("proveedor").get("id"), proveedorId)));

        return session.createQuery(query).uniqueResult();
    }

    public List<Producto> buscarConFiltros(Long tipoProductoId) {
        StringBuilder hql = new StringBuilder("FROM Producto p");

        // Lista de parámetros
        Map<String, Object> params = new HashMap<>();
        /*
         * if (proveedorId != null) {
         * hql.append(" AND p.proveedorId = :proveedorId");
         * params.put("proveedorId", proveedorId);
         * }
         */
        if (tipoProductoId != null) {
            hql.append(" WHERE p.tipoProducto.id = :tipoProductoId");
            params.put("tipoProductoId", tipoProductoId);
        }

        // Creamos la query
        Query query = sessionFactory.getCurrentSession()
                .createQuery(hql.toString(), Producto.class);

        // Asignamos parámetros dinámicamente
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }

        return query.getResultList();

    }

    public List<Producto> buscarPorProveedorId(Long proveedorId) {
        StringBuilder hql = new StringBuilder("FROM Producto p");

        // Lista de parámetros
        Map<String, Object> params = new HashMap<>();

        if (proveedorId != null) {
            hql.append(" WHERE p.proveedor.id = :proveedorId");
            params.put("proveedorId", proveedorId);
        }

        // Creamos la query
        Query query = sessionFactory.getCurrentSession()
                .createQuery(hql.toString(), Producto.class);

        // Asignamos parámetros dinámicamente
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }

        return query.getResultList();

    }

    public List<Producto> filtrarProductos(Long idProveedor, String busqueda, Long tipoProductoId, Long tipoVentanaId) {
        Session session = sessionFactory.getCurrentSession();

        String hql = "FROM Producto p WHERE p.proveedor.id = :idProveedor ";
        if (busqueda != null && !busqueda.trim().isEmpty())
            hql += "AND LOWER(p.nombre) LIKE LOWER(:busqueda) ";

        if (tipoProductoId != null)
            hql += "AND p.tipoProducto.id =:tipoProductoId";

        if (tipoVentanaId != null)
            hql += "AND p.tipoVentana.id =: tipoVentanaId";

        Query query = session.createQuery(hql, Producto.class);
        query.setParameter("idProveedor", idProveedor);

        if (busqueda != null && !busqueda.trim().isEmpty())
            query.setParameter("busqueda", "%" + busqueda + "%");

        if (tipoProductoId != null)
            query.setParameter("tipoProductoId", tipoProductoId);

        if (tipoVentanaId != null)
            query.setParameter("tipoVentanaId", tipoVentanaId);

        return query.getResultList();
    }

    public List<TipoProducto> obtenerTiposProductoPorProveedor(Long idProveedor) {
        Session session = sessionFactory.getCurrentSession();

        String hql = "SELECT DISTINCT p.tipoProducto FROM Producto p WHERE p.proveedor.id =: idProveedor AND p.tipoProducto IS NOT NULL";
        org.hibernate.Query<TipoProducto> query = session.createQuery(hql, TipoProducto.class);
        query.setParameter("idProveedor", idProveedor);

        return query.getResultList();
    }

    public List<TipoVentana> obtenerTiposVentanasPorProveedor(Long idProveedor) {
        Session session = sessionFactory.getCurrentSession();

        String hql = "SELECT DISTINCT p.tipoVentana FROM Producto p WHERE p.proveedor.id =: idProveedor AND p.tipoVentana IS NOT NULL";
        org.hibernate.Query<TipoVentana> query = session.createQuery(hql, TipoVentana.class);
        query.setParameter("idProveedor", idProveedor);

        return query.getResultList();
    }

    public List<Producto> buscarProductosParaCotizacion(
            Long tipoVentanaId,
            Long anchoId,
            Long altoId,
            Long materialDePerfilId,
            Long tipoDeVidrioId,
            Long colorId,
            Boolean conPremarco,
            Boolean conBarrotillos) {

        StringBuilder hql = new StringBuilder("FROM Producto p WHERE 1=1");
        Map<String, Object> params = new HashMap<>();

        // Filtros dinámicos

        if (tipoVentanaId != null) {
            hql.append(" AND p.tipoVentana.id = :tipoVentanaId");
            params.put("tipoVentanaId", tipoVentanaId);
        }

        if (anchoId != null) {
            hql.append(" AND p.ancho.id = :anchoId");
            params.put("anchoId", anchoId);
        }

        if (altoId != null) {
            hql.append(" AND p.alto.id = :altoId");
            params.put("altoId", altoId);
        }

        if (materialDePerfilId != null) {
            hql.append(" AND p.materialDePerfil.id = :materialDePerfilId");
            params.put("materialDePerfilId", materialDePerfilId);
        }

        if (tipoDeVidrioId != null) {
            hql.append(" AND p.tipoDeVidrio.id = :tipoDeVidrioId");
            params.put("tipoDeVidrioId", tipoDeVidrioId);
        }

        if (colorId != null) {
            hql.append(" AND p.color.id = :colorId");
            params.put("colorId", colorId);
        }

        // Opciones adicionales (si las tenés en Producto)
        if (conPremarco != null && conPremarco) {
            hql.append(" AND p.dimensiones.conPremarco = true");
        }

        if (conBarrotillos != null && conBarrotillos) {
            hql.append(" AND p.dimensiones.conBarrotillos = true");
        }

        // Crear query dinámica
        Query query = sessionFactory.getCurrentSession().createQuery(hql.toString(), Producto.class);

        // Seteo dinámico de parámetros
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }

        return query.getResultList();
    }

    public List<Producto> obtenertodosPorListadoId(List<Long> productosIds) {
        String hql = "FROM Producto p WHERE p.id IN :ids";
        Query query = sessionFactory.getCurrentSession().createQuery(hql, Producto.class);
        query.setParameter("ids", productosIds);
        return query.getResultList();
    }

    public List<ProductoGenericoDTO> obtenerProductosGenericos() {
        Session session = sessionFactory.getCurrentSession();

        String hql = "SELECT new com.tallerwebi.presentacion.dto.ProductoGenericoDTO(" +
                "p.tipoProducto.nombre, " +
                "COALESCE(p.tipoVentana.nombre, 'N/A'), " +
                "COALESCE(p.materialDePerfil.nombre, 'N/A'), " +
                "COALESCE(p.tipoDeVidrio.nombre,'N/A'), " +
                "COALESCE(CONCAT(p.ancho.nombre, 'x', p.alto.nombre), 'N/A'), " +
                "COUNT(DISTINCT p.proveedor.id)) " +
                "FROM Producto p " +
                "GROUP BY " +
                "p.tipoProducto.id, p.tipoVentana.id, p.materialDePerfil.id, p.tipoDeVidrio.id, p.ancho.id, p.alto.id";

        return session.createQuery(hql, ProductoGenericoDTO.class).list();
    }


    public List<Producto> filtrarProductos(
            Long tipoProductoId,
            Long tipoVentanaId,
            Long anchoId,
            Long altoId,
            Long materialPerfilId,
            Long colorId) {

        StringBuilder hql = new StringBuilder("select distinct p from Producto p ")
                .append("left join fetch p.tipoProducto ")
                .append("left join fetch p.tipoVentana ")
                .append("left join fetch p.marca ")
                .append("left join fetch p.ancho ")
                .append("left join fetch p.alto ")
                .append("left join fetch p.materialDePerfil ")
                .append("left join fetch p.color ");

        // Mapa para los parámetros dinámicos
        Map<String, Object> params = new HashMap<>();

        // Solo agregamos el "where" si hay algún filtro
        boolean tieneFiltro = false;

        if (tipoProductoId != null) {
            hql.append(tieneFiltro ? " and" : " where").append(" p.tipoProducto.id = :tipoProductoId");
            params.put("tipoProductoId", tipoProductoId);
            tieneFiltro = true;
        }
        if (tipoVentanaId != null) {
            hql.append(tieneFiltro ? " and" : " where").append(" p.tipoVentana.id = :tipoVentanaId");
            params.put("tipoVentanaId", tipoVentanaId);
            tieneFiltro = true;
        }
        if (anchoId != null) {
            hql.append(tieneFiltro ? " and" : " where").append(" p.ancho.id = :anchoId");
            params.put("anchoId", anchoId);
            tieneFiltro = true;
        }
        if (altoId != null) {
            hql.append(tieneFiltro ? " and" : " where").append(" p.alto.id = :altoId");
            params.put("altoId", altoId);
            tieneFiltro = true;
        }
        if (materialPerfilId != null) {
            hql.append(tieneFiltro ? " and" : " where").append(" p.materialDePerfil.id = :materialPerfilId");
            params.put("materialPerfilId", materialPerfilId);
            tieneFiltro = true;
        }
        if (colorId != null) {
            hql.append(tieneFiltro ? " and" : " where").append(" p.color.id = :colorId");
            params.put("colorId", colorId);
            tieneFiltro = true;
        }
      /*  if (nombre != null && !nombre.isEmpty()) {
            hql.append(tieneFiltro ? " and" : " where").append(" lower(p.nombre) like :nombre");
            params.put("nombre", "%" + nombre.toLowerCase() + "%");
            tieneFiltro = true;
        }*/

        // Creamos la consulta
        Query query = sessionFactory.getCurrentSession()
                .createQuery(hql.toString(), Producto.class);

        // Asignamos los parámetros
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }

        return query.getResultList();
    }

}

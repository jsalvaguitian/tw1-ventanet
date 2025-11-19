package com.tallerwebi.dominio.repositorios_interfaces;

import java.util.List;

import com.tallerwebi.dominio.entidades.Comentario;


public interface RepositorioComentario {
    Comentario obtenerPorId(Long id);
    List<Comentario> obtenerPorIdCotizacion(Long idCotizacion);
    List<Comentario> obtenerPorIdLicitacion(Long idLicitacion);
    Comentario guardar(Comentario comentario);
    void actualizar(Comentario comentario);
    List<Comentario> listarTodos();
    long contarNoLeidosParaCotizacionCliente(Long cotizacionId);
    long contarNoLeidosParaCotizacionProveedor(Long cotizacionId);
    long contarNoLeidosParaLicitacionCliente(Long licitacionId);
    long contarNoLeidosParaLicitacionProveedor(Long licitacionId);
}
 
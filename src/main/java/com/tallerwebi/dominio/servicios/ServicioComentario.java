package com.tallerwebi.dominio.servicios;

import java.util.List;

import com.tallerwebi.dominio.entidades.Comentario;

public interface ServicioComentario {
    List<Comentario> obtener();
    Comentario crearComentario(Comentario comentario);
    Comentario obtenerPorId(Long id);
    List<Comentario> obtenerComentarioPorIdCotizacion(Long idCotizacion);
    List<Comentario> obtenerComentarioPorIdLicitacion(Long idLicitacion);
    void marcarLeidosParaCliente(Long idCotizacion);
    void marcarLeidosParaProveedor(Long idCotizacion);
    void marcarLeidosParaClienteLicitacion(Long idLicitacion);
    void marcarLeidosParaProveedorLicitacion(Long idLicitacion);
    long contarNoLeidosParaCliente(Long idCotizacion);
    long contarNoLeidosParaProveedor(Long idCotizacion);
    long contarNoLeidosParaClienteLicitacion(Long idLicitacion);
    long contarNoLeidosParaProveedorLicitacion(Long idLicitacion);
}

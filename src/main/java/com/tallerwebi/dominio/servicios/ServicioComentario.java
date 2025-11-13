package com.tallerwebi.dominio.servicios;

import java.util.List;

import com.tallerwebi.dominio.entidades.Comentario;

public interface ServicioComentario {
    List<Comentario> obtener();
    Comentario crearComentario(Comentario comentario);
    Comentario obtenerPorId(Long id);
    List<Comentario> obtenerComentarioPorIdCotizacion(Long idCotizacion);
    void marcarLeidosParaCliente(Long idCotizacion);
    void marcarLeidosParaProveedor(Long idCotizacion);
    long contarNoLeidosParaCliente(Long idCotizacion);
    long contarNoLeidosParaProveedor(Long idCotizacion);
}

package com.tallerwebi.dominio.repositorios_interfaces;

import java.util.List;

import com.tallerwebi.dominio.entidades.Notificacion;

public interface RepositorioNotificacion {

    Notificacion guardar(Notificacion n);

    List<Notificacion> obtenerNoLeidasPorUsuario(Long usuarioId, int limit);

    List<Notificacion> obtenerTodasPorUsuario(Long usuarioId, int limit, int offset);

    void marcarComoLeida(Long id);

    void marcarTodasComoLeidas(Long usuarioId);

    Long contarNoLeidas(Long id);

}

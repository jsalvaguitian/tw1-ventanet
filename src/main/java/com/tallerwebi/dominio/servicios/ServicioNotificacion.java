package com.tallerwebi.dominio.servicios;

import java.util.List;

import com.tallerwebi.dominio.entidades.Notificacion;
import com.tallerwebi.dominio.entidades.Usuario;

public interface ServicioNotificacion {

    Notificacion notificar(Usuario destinatario, String mensaje, String url, String tipo, Long entidadId);

    List<Notificacion> obtenerNoLeidas(Long usuarioId, int limit);

    List<Notificacion> obtenerTodas(Long usuarioId, int limit, int offset);

    void marcarComoLeida(Long id);

    void marcarTodasComoLeidas(Long usuarioId);

    Long contarNoLeidas(Long id);

}

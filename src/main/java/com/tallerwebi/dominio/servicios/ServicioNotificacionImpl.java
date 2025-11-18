package com.tallerwebi.dominio.servicios;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tallerwebi.dominio.entidades.Notificacion;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.repositorios_interfaces.RepositorioNotificacion;

@Service
@Transactional
public class ServicioNotificacionImpl implements ServicioNotificacion {

    private final RepositorioNotificacion repositorioNotificacion;

    @Autowired
    public ServicioNotificacionImpl(RepositorioNotificacion repositorioNotificacion) {
        this.repositorioNotificacion = repositorioNotificacion;
    }

    @Override
    public Notificacion notificar(Usuario destinatario, String mensaje, String url, String tipo, Long entidadId) {
        Notificacion n = new Notificacion();
        n.setUsuario(destinatario);
        n.setMensaje(mensaje);
        n.setUrl(url);
        n.setTipo(tipo);
        n.setEntidadId(entidadId);
        return repositorioNotificacion.guardar(n);
    }

    @Override
    public List<Notificacion> obtenerNoLeidas(Long usuarioId, int limit) {
        return repositorioNotificacion.obtenerNoLeidasPorUsuario(usuarioId, limit);
    }

    @Override
    public List<Notificacion> obtenerTodas(Long usuarioId, int limit, int offset) {
        return repositorioNotificacion.obtenerTodasPorUsuario(usuarioId, limit, offset);
    }

    @Override
    public void marcarComoLeida(Long id) {
        repositorioNotificacion.marcarComoLeida(id);
    }

    @Override
    public void marcarTodasComoLeidas(Long usuarioId) {
        repositorioNotificacion.marcarTodasComoLeidas(usuarioId);
    }

    @Override
    public Long contarNoLeidas(Long id) {
        return repositorioNotificacion.contarNoLeidas(id);
    }
}

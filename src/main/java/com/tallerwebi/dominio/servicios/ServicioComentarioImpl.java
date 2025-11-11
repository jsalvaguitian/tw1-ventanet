package com.tallerwebi.dominio.servicios;

import java.time.LocalDateTime;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tallerwebi.dominio.entidades.Cliente;
import com.tallerwebi.dominio.entidades.Comentario;
import com.tallerwebi.dominio.entidades.Cotizacion;
import com.tallerwebi.dominio.entidades.Proveedor;
import com.tallerwebi.dominio.excepcion.NoHayComentarioExistente;
import com.tallerwebi.dominio.repositorios_interfaces.RepositorioComentario;

@Service
@Transactional
public class ServicioComentarioImpl implements ServicioComentario {
    private final RepositorioComentario repositorioComentario;
    private final ServicioEmail servicioEmail;

    @Autowired
    public ServicioComentarioImpl(RepositorioComentario repositorioComentario,
            ServicioEmail servicioEmail) {
        this.repositorioComentario = repositorioComentario;
        this.servicioEmail = servicioEmail;
    }

    @Override
    public List<Comentario> obtener() {
        return repositorioComentario.listarTodos();
    }

    @Override
    public Comentario crearComentario(Comentario comentario) {
        comentario.setFechaCreacion(LocalDateTime.now());
        Comentario guardado = repositorioComentario.guardar(comentario);

        // Enviar email a la contraparte
        Cotizacion cotizacion = comentario.getCotizacion();
        if (cotizacion != null) {
            Cliente cliente = cotizacion.getCliente();
            Proveedor proveedor = cotizacion.getProveedor();
            String asunto = "Nuevo mensaje en la cotización #" + cotizacion.getId();
            String cuerpoBase = "Se agregó un nuevo comentario: \n\n" + comentario.getMensaje();
            if (comentario.getCliente() != null) {
                // Lo escribió cliente, avisar a proveedor
                if (proveedor != null && proveedor.getEmail() != null) {
                    servicioEmail.enviarEmail(proveedor.getEmail(), asunto, cuerpoBase, false);
                }
            } else if (comentario.getProveedor() != null) {
                // Lo escribió proveedor, avisar a cliente
                if (cliente != null && cliente.getEmail() != null) {
                    servicioEmail.enviarEmail(cliente.getEmail(), asunto, cuerpoBase, false);
                }
            }
        }
        return guardado;
    }

    @Override
    public Comentario obtenerPorId(Long id) throws NoHayComentarioExistente {
        Comentario comentario = repositorioComentario.obtenerPorId(id);
        if (comentario == null) {
            throw new NoHayComentarioExistente("No se encontró el comentario con el ID: " + id);
        }
        return comentario;
    }

    @Override
    public List<Comentario> obtenerComentarioPorIdCotizacion(Long idCotizacion) {
        return repositorioComentario.obtenerPorIdCotizacion(idCotizacion);
    }

    @Override
    public void marcarLeidosParaCliente(Long idCotizacion) {
        var comentarios = repositorioComentario.obtenerPorIdCotizacion(idCotizacion);
        for (Comentario c : comentarios) {
            if (!c.isLeidoPorCliente()) {
                c.setLeidoPorCliente(true);
                repositorioComentario.actualizar(c);
            }
        }
    }

    @Override
    public void marcarLeidosParaProveedor(Long idCotizacion) {
        var comentarios = repositorioComentario.obtenerPorIdCotizacion(idCotizacion);
        for (Comentario c : comentarios) {
            if (!c.isLeidoPorProveedor()) {
                c.setLeidoPorProveedor(true);
                repositorioComentario.actualizar(c);
            }
        }
    }

    @Override
    public long contarNoLeidosParaCliente(Long idCotizacion) {
        return repositorioComentario.contarNoLeidosParaCotizacionCliente(idCotizacion);
    }

    @Override
    public long contarNoLeidosParaProveedor(Long idCotizacion) {
        return repositorioComentario.contarNoLeidosParaCotizacionProveedor(idCotizacion);
    }
}

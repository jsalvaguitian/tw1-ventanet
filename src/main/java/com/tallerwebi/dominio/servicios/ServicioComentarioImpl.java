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
import com.tallerwebi.dominio.entidades.Licitacion;
import com.tallerwebi.dominio.excepcion.NoHayComentarioExistente;
import com.tallerwebi.dominio.repositorios_interfaces.RepositorioComentario;

@Service
@Transactional
public class ServicioComentarioImpl implements ServicioComentario {
    private final RepositorioComentario repositorioComentario;
    private final ServicioEmail servicioEmail;
    private ServicioNotificacion servicioNotificacion;

    @Autowired
    public ServicioComentarioImpl(RepositorioComentario repositorioComentario,
            ServicioEmail servicioEmail, ServicioNotificacion servicioNotificacion) {
        this.repositorioComentario = repositorioComentario;
        this.servicioEmail = servicioEmail;
        this.servicioNotificacion = servicioNotificacion;
    }

    @Override
    public List<Comentario> obtener() {
        return repositorioComentario.listarTodos();
    }

    @Override
    public Comentario crearComentario(Comentario comentario) {
        comentario.setFechaCreacion(LocalDateTime.now());
        // Marcar como leído para el emisor para evitar que aparezca como no leído para quien lo envió
        if (comentario.getCliente() != null) {
            comentario.setLeidoPorCliente(true);
        }
        if (comentario.getProveedor() != null) {
            comentario.setLeidoPorProveedor(true);
        }
        Comentario guardado = repositorioComentario.guardar(comentario);

        // Enviar email a quien se le envia el comentario con cuerpo HTML
        Cotizacion cotizacion = comentario.getCotizacion();
        Licitacion licitacion = comentario.getLicitacion();
        if (cotizacion != null || licitacion != null) {
            boolean esCotizacion = cotizacion != null;
            Cliente cliente;
            Proveedor proveedor;
            if (esCotizacion) {
                cliente = cotizacion.getCliente();
                proveedor = cotizacion.getProveedor();
            } else {
                cliente = licitacion.getCliente();
                proveedor = licitacion.getProveedor();
            }
            String asunto = esCotizacion ?
                ("Nuevo mensaje en la cotización #" + cotizacion.getId()) :
                ("Nuevo mensaje en la licitación #" + licitacion.getId());

            String remitenteNombre;
            if (comentario.getCliente() != null && cliente != null) {
                remitenteNombre = (cliente.getNombre() != null ? cliente.getNombre() : "Cliente") +
                        (cliente.getApellido() != null ? " " + cliente.getApellido() : "");
            } else if (comentario.getProveedor() != null && proveedor != null) {
                remitenteNombre = (proveedor.getRazonSocial() != null ? proveedor.getRazonSocial() : "Proveedor");
            } else {
                remitenteNombre = "Usuario";
            }

                String estado = esCotizacion ? (cotizacion.getEstado() != null ? cotizacion.getEstado().toString() : "")
                              : (licitacion.getEstado() != null ? licitacion.getEstado().toString() : "");
                String monto = esCotizacion ? (cotizacion.getMontoTotal() != null ? cotizacion.getMontoTotal().toString() : "")
                            : (licitacion.getMontoTotal() != null ? licitacion.getMontoTotal().toString() : "");
            String fecha = comentario.getFechaCreacion() != null ? comentario.getFechaCreacion().toString()
                    : LocalDateTime.now().toString();
                String enlace = esCotizacion ? ("/cotizacion/" + cotizacion.getId() + "/mensajes") : ("/licitacion/" + licitacion.getId() + "/mensajes");

                Long idEntidad = esCotizacion ? cotizacion.getId() : licitacion.getId();
                String etiquetaEntidad = esCotizacion ? "Cotización" : "Licitación";
                StringBuilder cuerpo = new StringBuilder();
                cuerpo.append("<div style='font-family:Segoe UI,Arial,sans-serif;font-size:14px;color:#222'>")
                    .append("<h2 style='color:#0b335b;margin-top:0'>Nuevo mensaje en tu ")
                    .append(etiquetaEntidad.toLowerCase())
                    .append("</h2>")
                    .append("<p><strong>Remitente:</strong> ").append(remitenteNombre).append("</p>")
                    .append("<p><strong>").append(etiquetaEntidad).append(":</strong> #").append(idEntidad).append("</p>")
                    .append("<p><strong>Estado actual:</strong> ").append(estado).append("</p>")
                    .append("<p><strong>Monto total:</strong> ").append(monto).append("</p>")
                    .append("<p><strong>Fecha del mensaje:</strong> ").append(fecha).append("</p>")
                    .append("<hr style='border:none;border-top:1px solid #ddd;margin:16px 0'>")
                    .append("<p style='white-space:pre-line'><strong>Mensaje:</strong><br>")
                    .append(escapeHtml(comentario.getMensaje())).append("</p>")
                    .append("<p style='margin-top:24px'>Puedes responder ingresando al siguiente enlace (inicia sesión si es necesario):<br>")
                    .append("<a style='color:#1a73e8' href='http://localhost:8080/spring").append(enlace)
                    .append("'>Abrir conversación</a></p>")
                    .append("<p style='font-size:12px;color:#555'>Este es un correo automático de Ventanet. Por favor no respondas a este email.</p>")
                    .append("</div>");

            String cuerpoHtml = cuerpo.toString();
            if (comentario.getCliente() != null) {
                if (proveedor != null && proveedor.getEmail() != null) {
                    servicioEmail.enviarEmail(proveedor.getEmail(), asunto, cuerpoHtml, true);
                }
                if (proveedor != null) {
                    String txt = "Nuevo mensaje de " + (cliente != null ? cliente.getNombre() : "Cliente") + (esCotizacion ? " en la cotización #" + cotizacion.getId() : " en la licitación #" + licitacion.getId());
                    servicioNotificacion.notificar(proveedor, txt,
                        "/spring" + enlace,
                        "MENSAJE_NUEVO", esCotizacion ? cotizacion.getId() : licitacion.getId());
                }
            } else if (comentario.getProveedor() != null) {
                if (cliente != null && cliente.getEmail() != null) {
                    servicioEmail.enviarEmail(cliente.getEmail(), asunto, cuerpoHtml, true);
                }
                if (cliente != null) {
                    String txt = "Nuevo mensaje del proveedor " + (proveedor != null ? proveedor.getRazonSocial() : "Proveedor") + (esCotizacion ? " en la cotización #" + cotizacion.getId() : " en la licitación #" + licitacion.getId());
                    servicioNotificacion.notificar(cliente, txt,
                        "/spring" + enlace,
                        "MENSAJE_NUEVO", esCotizacion ? cotizacion.getId() : licitacion.getId());
                }
            }

        }
        return guardado;
    }

    // Escapar HTML básico para evitar inyección si se envía contenido de usuario
    private String escapeHtml(String input) {
        if (input == null)
            return "";
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
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

    public List<Comentario> obtenerComentarioPorIdLicitacion(Long idLicitacion) {
        return repositorioComentario.obtenerPorIdLicitacion(idLicitacion);
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

    public void marcarLeidosParaClienteLicitacion(Long idLicitacion) {
        var comentarios = repositorioComentario.obtenerPorIdLicitacion(idLicitacion);
        for (Comentario c : comentarios) {
            if (!c.isLeidoPorCliente()) {
                c.setLeidoPorCliente(true);
                repositorioComentario.actualizar(c);
            }
        }
    }

    public void marcarLeidosParaProveedorLicitacion(Long idLicitacion) {
        var comentarios = repositorioComentario.obtenerPorIdLicitacion(idLicitacion);
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

    public long contarNoLeidosParaClienteLicitacion(Long idLicitacion) {
        return repositorioComentario.contarNoLeidosParaLicitacionCliente(idLicitacion);
    }

    public long contarNoLeidosParaProveedorLicitacion(Long idLicitacion) {
        return repositorioComentario.contarNoLeidosParaLicitacionProveedor(idLicitacion);
    }
}

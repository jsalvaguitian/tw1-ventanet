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

        // Enviar email a quien se le envia el comentario con cuerpo HTML
        Cotizacion cotizacion = comentario.getCotizacion();
        if (cotizacion != null) {
            Cliente cliente = cotizacion.getCliente();
            Proveedor proveedor = cotizacion.getProveedor();
            String asunto = "Nuevo mensaje en la cotización #" + cotizacion.getId();

            String remitenteNombre;
            if (comentario.getCliente() != null && cliente != null) {
                remitenteNombre = (cliente.getNombre() != null ? cliente.getNombre() : "Cliente") +
                        (cliente.getApellido() != null ? " " + cliente.getApellido() : "");
            } else if (comentario.getProveedor() != null && proveedor != null) {
                remitenteNombre = (proveedor.getRazonSocial() != null ? proveedor.getRazonSocial() : "Proveedor");
            } else {
                remitenteNombre = "Usuario";
            }

            String estado = cotizacion.getEstado() != null ? cotizacion.getEstado().toString() : "";
            String monto = cotizacion.getMontoTotal() != null ? cotizacion.getMontoTotal().toString() : "";
            String fecha = comentario.getFechaCreacion() != null ? comentario.getFechaCreacion().toString() : LocalDateTime.now().toString();
            String enlace = "/cotizacion/" + cotizacion.getId() + "/mensajes"; // relativo

            StringBuilder cuerpo = new StringBuilder();
            cuerpo.append("<div style='font-family:Segoe UI,Arial,sans-serif;font-size:14px;color:#222'>")
                  .append("<h2 style='color:#0b335b;margin-top:0'>Nuevo mensaje en tu cotización</h2>")
                  .append("<p><strong>Remitente:</strong> ").append(remitenteNombre).append("</p>")
                  .append("<p><strong>Cotización:</strong> #").append(cotizacion.getId()).append("</p>")
                  .append("<p><strong>Estado actual:</strong> ").append(estado).append("</p>")
                  .append("<p><strong>Monto total:</strong> ").append(monto).append("</p>")
                  .append("<p><strong>Fecha del mensaje:</strong> ").append(fecha).append("</p>")
                  .append("<hr style='border:none;border-top:1px solid #ddd;margin:16px 0'>")
                  .append("<p style='white-space:pre-line'><strong>Mensaje:</strong><br>")
                  .append(escapeHtml(comentario.getMensaje())).append("</p>")
                  .append("<p style='margin-top:24px'>Puedes responder ingresando al siguiente enlace (inicia sesión si es necesario):<br>")
                  .append("<a style='color:#1a73e8' href='http://localhost:8080/spring").append(enlace).append("'>Abrir conversación</a></p>")
                  .append("<p style='font-size:12px;color:#555'>Este es un correo automático de Ventanet. Por favor no respondas a este email.</p>")
                  .append("</div>");

            String cuerpoHtml = cuerpo.toString();
            if (comentario.getCliente() != null) {
                // Lo escribió cliente, avisar a proveedor
                if (proveedor != null && proveedor.getEmail() != null) {
                    servicioEmail.enviarEmail(proveedor.getEmail(), asunto, cuerpoHtml, true);
                }
            } else if (comentario.getProveedor() != null) {
                // Lo escribió proveedor, avisar a cliente
                if (cliente != null && cliente.getEmail() != null) {
                    servicioEmail.enviarEmail(cliente.getEmail(), asunto, cuerpoHtml, true);
                }
            }
        }
        return guardado;
    }

    // Escapar HTML básico para evitar inyección si se envía contenido de usuario
    private String escapeHtml(String input) {
        if (input == null) return "";
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

package com.tallerwebi.dominio.servicios;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tallerwebi.dominio.enums.EstadoCotizacion;
import com.tallerwebi.dominio.excepcion.CotizacionesExistente;
import com.tallerwebi.dominio.excepcion.NoHayCotizacionExistente;
import com.tallerwebi.dominio.repositorios_interfaces.*;
import com.tallerwebi.infraestructura.*;
import com.tallerwebi.dominio.entidades.*;

@Service
@Transactional
public class ServicioCotizacionImpl implements ServicioCotizacion {
    private final RepositorioCotizacion cotizacionRepository;
    private final RepositorioProductoImpl productoRepository;
    private ServicioEmail servicioEmail;
    private ServicioNotificacion servicioNotificacion;

    @Autowired
    public ServicioCotizacionImpl(RepositorioCotizacion cotizacionRepository,
            RepositorioProductoImpl repositorioProducto, ServicioEmail servicioEmail,
            ServicioNotificacion servicioNotificacion) {
        this.cotizacionRepository = cotizacionRepository;
        this.productoRepository = repositorioProducto;
        this.servicioEmail = servicioEmail;
        this.servicioNotificacion = servicioNotificacion;
    }

    @Override
    public Cotizacion obtenerPorId(Long id) throws NoHayCotizacionExistente {
        Cotizacion cotizacion = cotizacionRepository.obtenerPorId(id);
        if (cotizacion == null) {
            throw new NoHayCotizacionExistente("No se encontró la cotización con ID: " + id);
        }
        return cotizacion;
    }

    @Override
    public List<Cotizacion> obtenerPorIdProveedor(Long proveedorId) {
        return cotizacionRepository.obtenerPorIdProveedor(proveedorId);
    }

    @Override
    public void actualizarEstado(Long cotizacionId, EstadoCotizacion estado) throws CotizacionesExistente {

        Cotizacion cotizacion = cotizacionRepository.obtenerPorId(cotizacionId);
        if (cotizacion == null) {
            throw new CotizacionesExistente();
        }

        cotizacion.setEstado(estado);
        cotizacionRepository.actualizarEstado(cotizacion);

        String texto = "";
        if (estado == EstadoCotizacion.APROBADA) {
            texto = "El proveedor " + cotizacion.getProveedor().getRazonSocial() + " aceptó tu cotización #"
                    + cotizacion.getId();
        } else if (estado == EstadoCotizacion.RECHAZADO) {
            texto = "Tu cotización #" + cotizacion.getId() + " de " + cotizacion.getProveedor().getRazonSocial()
                    + " fue rechazada";
        } else if (estado == EstadoCotizacion.COMPLETADA) {
            texto = "Tu cotización #" + cotizacion.getId() + " fue completada.";
        }
        servicioNotificacion.notificar(cotizacion.getCliente(), texto,
                "/spring/cliente/dashboard",
                "COTIZACION_ESTADO", cotizacion.getId());

    }

    @Override
    @Transactional
    public List<Cotizacion> obtenerCotizacionPorIdCliente(Long id) throws NoHayCotizacionExistente {
        System.out.println("[ServicioCotizacion] buscar cotizaciones para clienteId=" + id);
        List<Cotizacion> cotizaciones = cotizacionRepository.obtenerPorIdCliente(id);
        System.out.println(
                "[ServicioCotizacion] cantidad encontrada=" + (cotizaciones == null ? 0 : cotizaciones.size()));
        if (cotizaciones == null) {
            throw new NoHayCotizacionExistente();
        }
        return cotizaciones;
    }

    @Override
    public Cotizacion guardar(Cotizacion cotizacion) {
        Cotizacion newCotizacion = cotizacionRepository.guardar(cotizacion);

        String asunto = "VENTANET - Nueva Cotización Creada";
        String cuerpoProveedor = "Se ha creado una nueva cotización con ID: " + newCotizacion.getId() +
                "\nMonto Total: " + newCotizacion.getMontoTotal() +
                "\nEstado: " + newCotizacion.getEstado().name() +
                "\nCliente: " + newCotizacion.getCliente().getNombre() +
                "\nEmail Cliente: " + newCotizacion.getCliente().getEmail();

        // servicioEmail.enviarEmail(newCotizacion.getProveedor().getEmail(), asunto,
        // cuerpoProveedor, false);

        // inyectar ServicioNotificacion en el constructor y usar:
        String msg = "Se creó la cotización #" + newCotizacion.getId() + " para el proveedor "
                + newCotizacion.getProveedor().getRazonSocial();
        servicioNotificacion.notificar(newCotizacion.getCliente(), msg,
                "/spring/cliente/dashboard",
                "COTIZACION_CREATED", newCotizacion.getId());

        String msgProveedor = "El cliente " + newCotizacion.getCliente().getNombre() + " inicio la cotización #"
                + cotizacion.getId();
        servicioNotificacion.notificar(newCotizacion.getProveedor(), msgProveedor,
                "/spring/proveedor/dashboard-proveedor",
                "COTIZACION_CREATED", newCotizacion.getId());

        String cuerpoCliente = "Se ha creado una nueva cotización con ID: " + newCotizacion.getId() +
                "\nMonto Total: " + newCotizacion.getMontoTotal() +
                "\nEstado: " + newCotizacion.getEstado().name() +
                "\nProveedor: " + newCotizacion.getProveedor().getRazonSocial() +
                "\nEmail Proveedor: " + newCotizacion.getProveedor().getEmail();
        // servicioEmail.enviarEmail(newCotizacion.getCliente().getEmail(), asunto,
        // cuerpoCliente, false);
        return newCotizacion;
    }

    @Override
    public void registrarCotizacion(Cliente cliente, Map<Long, Integer> productosEnSesion) {
        if (productosEnSesion == null || productosEnSesion.isEmpty()) {
            throw new IllegalArgumentException("No hay productos en la cotización");
        }

        Cotizacion cotizacion = new Cotizacion();
        cotizacion.setCliente(cliente);
        cotizacion.setFechaCreacion(LocalDate.now());
        cotizacion.setEstado(EstadoCotizacion.PENDIENTE);

        double total = 0.0;
        Proveedor proveedor = null;

        for (Map.Entry<Long, Integer> entry : productosEnSesion.entrySet()) {
            Long productoId = entry.getKey();
            Integer cantidad = entry.getValue();

            Producto producto = productoRepository.obtener(productoId);

            if (proveedor == null) {
                proveedor = producto.getProveedor(); // Todos del mismo proveedor
                cotizacion.setProveedor(proveedor);
            }

            CotizacionItem item = new CotizacionItem();
            item.setProducto(producto);
            item.setCantidad(cantidad);
            item.setPrecioUnitario(producto.getPrecio());
            item.setCotizacion(cotizacion);

            cotizacion.getItems().add(item);

            total += producto.getPrecio() * cantidad;
        }

        cotizacion.setMontoTotal(total);

        cotizacionRepository.guardar(cotizacion);
    }

    public Map<String, Long> obtenerEstadisticasCotizacionesDelProveedor(Long proveedorId) {
        Map<String, Long> estadisticas = new HashMap<>();
        estadisticas.put("APROBADA", cotizacionRepository.contarCotizacionesAprobadasPorProveedor(proveedorId));
        estadisticas.put("PENDIENTE", cotizacionRepository.contarCotizacionesPendientesPorProveedor(proveedorId));
        estadisticas.put("RECHAZADO", cotizacionRepository.contarCotizacionesRechazadasPorProveedor(proveedorId));
        estadisticas.put("COMPLETADA", cotizacionRepository.contarCotizacionesCompletadasPorProveedor(proveedorId));
        return estadisticas;
    }

    public Map<String, Object> obtenerEstadisticaComparacionEntreProveedores(Long proveedorId) {
        Double promedioGeneral = cotizacionRepository.obtenerPromedioGeneralCompletadas();
        Long cantidadAprobadasProveedor = cotizacionRepository.contarCotizacionesAprobadasPorProveedor(proveedorId);

        Map<String, Object> comparacion = new HashMap<>();
        comparacion.put("proveedor", cantidadAprobadasProveedor);
        comparacion.put("promedio", promedioGeneral);

        return comparacion;
    }

    @Transactional
    public Map<String, Long> obtenerProductosMasCotizados(Long proveedorId) {
        List<Object[]> resultados = cotizacionRepository.obtenerProductosMasCotizados(proveedorId);
        Map<String, Long> productos = new LinkedHashMap<>();
        for (Object[] fila : resultados) {
            productos.put((String) fila[0], (Long) fila[1]);
        }
        return productos;
    }

    @Override
    public Map<String, Long> obtenerProductosMasCotizadosDeTodosLosProveedores() {
        List<Object[]> resultados = cotizacionRepository.obtenerProductosMasCotizadosDeTodosLosProveedores();
        Map<String, Long> productos = new LinkedHashMap<>();
        for (Object[] fila : resultados) {
            String nombre = (String) fila[0];
            Long cantidad = ((Number) fila[1]).longValue();
            productos.put(nombre, cantidad);
        }
        return productos;
    }
}

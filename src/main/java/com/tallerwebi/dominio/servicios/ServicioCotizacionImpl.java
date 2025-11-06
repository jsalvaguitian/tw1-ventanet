package com.tallerwebi.dominio.servicios;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.tallerwebi.dominio.enums.EstadoCotizacion;
import com.tallerwebi.dominio.excepcion.CotizacionesExistente;
import com.tallerwebi.dominio.excepcion.NoHayProductoExistente;
import com.tallerwebi.infraestructura.RepositorioCotizacionImpl;
import com.tallerwebi.dominio.repositorios_interfaces.*;
import com.tallerwebi.infraestructura.*;
import com.tallerwebi.dominio.entidades.*;
@Service
@Transactional
public class ServicioCotizacionImpl implements ServicioCotizacion {
     private final RepositorioCotizacion cotizacionRepository;
    private final RepositorioProductoImpl productoRepository;

     @Autowired
    public ServicioCotizacionImpl(RepositorioCotizacionImpl cotizacionRepository, RepositorioProductoImpl repositorioProducto) {
        this.cotizacionRepository = cotizacionRepository;
        this.productoRepository= repositorioProducto;
    }

    @Override
    public Cotizacion obtenerPorId(Long id) {
        Cotizacion cotizacion = cotizacionRepository.obtenerPorId(id);
        if (cotizacion == null) {
            throw new NoHayProductoExistente();
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
    }

    @Override
    @Transactional
    public List<Cotizacion> obtenerCotizacionPorIdCliente(Long id) {
        System.out.println("[ServicioCotizacion] buscar cotizaciones para clienteId=" + id);
        List<Cotizacion> cotizaciones = cotizacionRepository.obtenerPorIdCliente(id);
        System.out.println("[ServicioCotizacion] cantidad encontrada=" + (cotizaciones == null ? 0 : cotizaciones.size()));
        if (cotizaciones == null || cotizaciones.isEmpty()) {
            throw new NoHayProductoExistente();
        }
        return cotizaciones;
    }

    @Override
    public Cotizacion guardar(Cotizacion cotizacion) {
        return cotizacionRepository.guardar(cotizacion);
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

}

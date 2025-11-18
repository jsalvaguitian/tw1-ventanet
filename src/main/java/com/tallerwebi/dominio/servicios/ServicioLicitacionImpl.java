package com.tallerwebi.dominio.servicios;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.tallerwebi.dominio.entidades.Licitacion;
import com.tallerwebi.dominio.entidades.Producto;
import com.tallerwebi.dominio.entidades.ProductoCustom;
import com.tallerwebi.dominio.enums.EstadoLicitacion;
import com.tallerwebi.dominio.excepcion.NoHayLicitacionesExistentes;
import com.tallerwebi.infraestructura.RepositorioLicitacionImpl;
import com.tallerwebi.infraestructura.RepositorioProductoCustomImpl;

@Service("servicioLicitacion")
@Transactional
public class ServicioLicitacionImpl implements ServicioLicitacion {

    private final RepositorioLicitacionImpl licitacionRepository;
    private final RepositorioProductoCustomImpl productoCustomRepository;
    private ServicioNotificacion servicioNotificacion;

    public ServicioLicitacionImpl(RepositorioLicitacionImpl licitacionRepository,
            RepositorioProductoCustomImpl productoCustomRepository, ServicioNotificacion servicioNotificacion) {
        this.licitacionRepository = licitacionRepository;
        this.productoCustomRepository = productoCustomRepository;
        this.servicioNotificacion = servicioNotificacion;
    }

    @Override
    public void crear(Licitacion licitacion) {
        licitacionRepository.guardar(licitacion);

        String texto = "Iniciaste la licitacion #" + licitacion.getId();
        servicioNotificacion.notificar(licitacion.getCliente(), texto, "/spring/cliente/dashboard-custom",
                "LICITACION_ESTADO", licitacion.getId());

        String textoProveedor = "El Cliente " + licitacion.getCliente().getNombre() + " inicio la licitacion #"
                + licitacion.getId();
        servicioNotificacion.notificar(licitacion.getProveedor(), textoProveedor,
                "/spring/proveedor/dashboard-proveedor-custom",
                "LICITACION_ESTADO", licitacion.getId());
    }

    @Override
    public Licitacion obtenerPorId(Long id) {
        Licitacion licitacion = licitacionRepository.obtenerPorId(id);

        return licitacion;
    }

    @Override
    public void actualizar(Licitacion licitacion) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'actualizar'");
    }

    @Override
    public void eliminar(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'eliminar'");
    }

    @Override
    public void actualizarEstado(Long licitacionId, EstadoLicitacion estado) {

        Licitacion licitacion = licitacionRepository.obtenerPorId(licitacionId);

        licitacion.setEstado(estado);
        licitacionRepository.actualizarEstado(licitacion);
    }

    @Override
    public void actualizarEstadoYPrecioUnitario(Long licitacionId, EstadoLicitacion estado, Double precioUnitario) {

        Licitacion licitacion = licitacionRepository.obtenerPorId(licitacionId);
        licitacion.setEstado(estado);
        ProductoCustom productoCustom = licitacion.getProductoCustom();
        productoCustom.setPrecio(precioUnitario);
        productoCustomRepository.actualizar(productoCustom);
        licitacion.setProductoCustom(productoCustom);
        licitacionRepository.actualizarEstado(licitacion);

        String texto = "El proveedor " + licitacion.getProveedor().getRazonSocial() + " respondió tu liquidación #"
                + licitacion.getId() + " - " + estado.name();
        servicioNotificacion.notificar(licitacion.getCliente(), texto, "/spring/cliente/dashboard-custom",
                "LICITACION_ESTADO", licitacion.getId());

    }

    @Override
    public List<Licitacion> obtenerLicitacionesPorIdCliente(Long clienteId) throws NoHayLicitacionesExistentes {
        List<Licitacion> licitaciones = licitacionRepository.obtenerPorIdCliente(clienteId);

        if (licitaciones == null) {
            throw new NoHayLicitacionesExistentes();
        }
        return licitaciones;
    }

    @Override
    public List<Licitacion> obtenerLicitacionesPorIdProveedor(Long proveedorId) throws NoHayLicitacionesExistentes {
        List<Licitacion> licitaciones = licitacionRepository.obtenerPorIdProveedor(proveedorId);

        if (licitaciones == null) {
            throw new NoHayLicitacionesExistentes();
        }
        return licitaciones;
    }

}

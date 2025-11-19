package com.tallerwebi.dominio.servicios;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tallerwebi.dominio.entidades.MedioDePago;
import com.tallerwebi.dominio.entidades.Proveedor;
import com.tallerwebi.dominio.enums.EstadoUsuario;
import com.tallerwebi.dominio.enums.Rubro;
import com.tallerwebi.dominio.repositorios_interfaces.RepositorioMedioDePago;
import com.tallerwebi.dominio.repositorios_interfaces.RepositorioProveedor;


@Service
@Transactional
public class ServicioProveedorImpl implements ServicioProveedorI{
    @Autowired
    private RepositorioProveedor repositorioProveedor;
    private ServicioEmail servicioEmail;
    private RepositorioMedioDePago medioDePagoRepositorio;

    public ServicioProveedorImpl(RepositorioProveedor repositorioProveedor,ServicioEmail servicioEmail, RepositorioMedioDePago medioDePagoRepositorio) {
        this.repositorioProveedor = repositorioProveedor;
        this.servicioEmail = servicioEmail;
        this.medioDePagoRepositorio = medioDePagoRepositorio;
    }



    @Override
    public List<Proveedor> obtenerTodosLosProveedoresActivos() {
        return repositorioProveedor.obtenerTodosLosNombresProveedoresActivos();
    }

    @Override
    public List<Proveedor> obtenerTodosLosProveedoresPendientes() {
        return repositorioProveedor.obtenerTodosLosProveedoresPendientes();
    }



    @Override
    public Proveedor buscarPorId(Long id) {
        return repositorioProveedor.buscarPorId(id);
    }

    @Override
    public void actualizar(Proveedor proveedor) {
        repositorioProveedor.actualizar(proveedor);
    }

    @Override
    public Proveedor obtenerPorIdUsuario(Long idUsuario) {
        Proveedor proveedor = repositorioProveedor.buscarProveedorPorIdUsuario(idUsuario);
        if (proveedor == null) {
            throw new IllegalStateException("El usuario no corresponde a un proveedor válido");
        }        
        return proveedor;
    }

    @Override
    public Integer contarProveedores(EstadoUsuario estado) {
        if(estado == null){
            return repositorioProveedor.contarProveedores();
        }else{
            return repositorioProveedor.contarProveedores(estado);
        }
    }

    @Override
    public List<Rubro> obtenerRubrosActivos() {
        return repositorioProveedor.obtenerRubrosActivos();
    }

    @Override
    public List<Proveedor> obtenerProveedoresPorRubro(Rubro rubro) {
        return repositorioProveedor.listarPorRubro(rubro);
    }

    @Override
    public List<Proveedor> obtenerProveedoresPorEstadoActivoInactivo(Boolean estado) {
        return repositorioProveedor.listarTodosPorEstado(estado);
    }
   
    @Override
    public List<MedioDePago> obtenerMediosDePagoDeProveedor(Long proveedorId) {
        Proveedor proveedor = repositorioProveedor.obtenerProveedorConMedios(proveedorId);
        return proveedor.getMediosDePago();
    }

    @Override
    public void actualizarMediosPago(Long proveedorId, List<Long> medioIds) {
        Proveedor proveedor = repositorioProveedor.buscarPorId(proveedorId);

        proveedor.getMediosDePago().clear();

        for (Long medioId : medioIds) {
            MedioDePago medio = medioDePagoRepositorio.buscarPorId(medioId);
            if(medio != null){
                proveedor.getMediosDePago().add(medio);
            }            
        }

        repositorioProveedor.actualizar(proveedor);
    }

}

package com.tallerwebi.dominio.servicios;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.tallerwebi.dominio.entidades.Proveedor;
import com.tallerwebi.dominio.enums.EstadoUsuario;
import com.tallerwebi.dominio.repositorios_interfaces.RepositorioProveedor;


@Service
@Transactional
public class ServicioProveedorImpl implements ServicioProveedorI{
    @Autowired
    private RepositorioProveedor repositorioProveedor;
    

    public ServicioProveedorImpl(RepositorioProveedor repositorioProveedor) {
        this.repositorioProveedor = repositorioProveedor;
    }



    @Override
    public List<Proveedor> obtenerTodosLosProveedoresActivos() {
        return repositorioProveedor.obtenerTodosLosNombresProveedoresActivos();
    }
  /* 
  @Override
    public Proveedor obtenerPorIdUsuario(Long idUsuario) {
        Proveedor proveedor = repositorioProveedor.buscarProveedorPorIdUsuario(idUsuario);
        if (proveedor == null) {
            throw new IllegalStateException("El usuario no corresponde a un proveedor válido");
        }        
        return proveedor;
    }
*/


    



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
   




}

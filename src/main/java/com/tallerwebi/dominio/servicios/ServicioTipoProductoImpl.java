package com.tallerwebi.dominio.servicios;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.tallerwebi.dominio.entidades.TipoProducto;
import com.tallerwebi.dominio.excepcion.NoHayProductoExistente;
import com.tallerwebi.infraestructura.RepositorioTipoProductoImpl;

@Service("servicioTipoProducto")
@Transactional
public class ServicioTipoProductoImpl implements ServicioTipoProducto {
    private final RepositorioTipoProductoImpl  tipoProductoRepository;    

    public ServicioTipoProductoImpl(RepositorioTipoProductoImpl tipoProductoRepository) {
        this.tipoProductoRepository = tipoProductoRepository;
    }

     @Override
    public void actualizar(TipoProducto tipoProducto) {
        TipoProducto tipoProductoObt = tipoProductoRepository.obtener(tipoProducto.getId());
        if (tipoProductoObt == null) {
            throw new NoHayProductoExistente();
        }
        tipoProductoRepository.actualizar(tipoProducto);
    }

    @Override
    public void eliminar(Long id) {
        TipoProducto tipoProductoObt = tipoProductoRepository.obtener(id);
        if (tipoProductoObt == null) {
            throw new NoHayProductoExistente();
        }
        tipoProductoRepository.eliminar(id);
    }

    @Override
    public List<TipoProducto> obtener() {
        return this.tipoProductoRepository.obtener();
        
    }

    @Override
    public void crearTipoProducto(TipoProducto tipoProducto) {        
       this.tipoProductoRepository.guardar(tipoProducto);   
    }

    @Override
    public TipoProducto obtenerPorId(Long id) {
        TipoProducto tipoProducto = tipoProductoRepository.obtener(id);
        if (tipoProducto == null) {
            throw new NoHayProductoExistente();
        }
        return tipoProducto;
    }
    
}

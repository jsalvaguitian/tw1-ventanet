package com.tallerwebi.dominio.servicios;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tallerwebi.dominio.entidades.Marca;
import com.tallerwebi.dominio.excepcion.NoHayProductoExistente;
import com.tallerwebi.infraestructura.RepositorioMarcaImpl;

@Service("servicioMarca")
@Transactional
public class ServicioMarcaImpl implements ServicioMarca {
    private final RepositorioMarcaImpl  marcaRepository;    

    public ServicioMarcaImpl(RepositorioMarcaImpl marcaRepository) {
        this.marcaRepository = marcaRepository;
    }

     @Override
    public void actualizar(Marca producto) {
        Marca productoObt = marcaRepository.obtener(producto.getId());
        if (productoObt == null) {
            throw new NoHayProductoExistente();
        }
        marcaRepository.actualizar(producto);
    }

    @Override
    public void eliminar(Long id) {
        Marca marcaObt = marcaRepository.obtener(id);
        if (marcaObt == null) {
            throw new NoHayProductoExistente();
        }
        marcaRepository.eliminar(id);
    }

    @Override
    public List<Marca> obtener() {
        return this.marcaRepository.obtener();
        
    }

    @Override
    public void crearMarca(Marca presentacion) {        
       this.marcaRepository.guardar(presentacion);   
    }

    @Override
    public Marca obtenerPorId(Long id) {
        Marca presentacion = marcaRepository.obtener(id);
        if (presentacion == null) {
            throw new NoHayProductoExistente();
        }
        return presentacion;
    }
}


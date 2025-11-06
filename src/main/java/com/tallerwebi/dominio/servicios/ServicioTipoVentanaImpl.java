package com.tallerwebi.dominio.servicios;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;
import com.tallerwebi.dominio.entidades.TipoVentana;
import com.tallerwebi.dominio.excepcion.NoHayProductoExistente;
import com.tallerwebi.infraestructura.RepositorioTipoVentanaImpl;
import com.tallerwebi.presentacion.dto.TipoVentanaDTO;

@Service("servicioTipoVentana")
@Transactional
public class ServicioTipoVentanaImpl implements ServicioTipoVentana {
    private final RepositorioTipoVentanaImpl tipoVentanaRepository;

    public ServicioTipoVentanaImpl(RepositorioTipoVentanaImpl tipoVentanaRepository) {
        this.tipoVentanaRepository = tipoVentanaRepository;
    }

    @Override
    public List<TipoVentana> obtener() {
        return this.tipoVentanaRepository.obtener();
    }

    @Override
    public void crearTipoVentana(TipoVentana ventana) {
        tipoVentanaRepository.guardar(ventana);
    }

    @Override
    public List<TipoVentana> obtenerPorIdTipoProducto(Long id_tipo_producto) {
        List<TipoVentana> tipoVentanas = tipoVentanaRepository.obtenerPorIdTipoProducto(id_tipo_producto);
        if (tipoVentanas == null || tipoVentanas.isEmpty()) {
            throw new NoHayProductoExistente();
        }
        return tipoVentanas;
    }

   
    


}

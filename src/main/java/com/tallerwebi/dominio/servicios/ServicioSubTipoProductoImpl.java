package com.tallerwebi.dominio.servicios;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;
import com.tallerwebi.dominio.entidades.SubTipoProducto;
import com.tallerwebi.dominio.excepcion.NoHayProductoExistente;
import com.tallerwebi.infraestructura.RepositorioSubTipoProductoImpl;
import com.tallerwebi.presentacion.dto.TipoVentanaDTO;

@Service("servicioTipoVentana")
@Transactional
public class ServicioSubTipoProductoImpl implements ServicioSubTipoProducto {
    private final RepositorioSubTipoProductoImpl tipoVentanaRepository;

    public ServicioSubTipoProductoImpl(RepositorioSubTipoProductoImpl tipoVentanaRepository) {
        this.tipoVentanaRepository = tipoVentanaRepository;
    }

    @Override
    public List<SubTipoProducto> obtener() {
        return this.tipoVentanaRepository.obtener();
    }

    @Override
    public void crearTipoVentana(SubTipoProducto ventana) {
        tipoVentanaRepository.guardar(ventana);
    }

    @Override
    public List<SubTipoProducto> obtenerPorIdTipoProducto(Long id_tipo_producto) {
        List<SubTipoProducto> tipoVentanas = tipoVentanaRepository.obtenerPorIdTipoProducto(id_tipo_producto);
        if (tipoVentanas == null || tipoVentanas.isEmpty()) {
            throw new NoHayProductoExistente();
        }
        return tipoVentanas;
    }

   
    


}

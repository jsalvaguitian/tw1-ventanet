package com.tallerwebi.dominio.servicios;

import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.tallerwebi.dominio.entidades.Producto;

@Service("ServicioProdV2")
@Transactional
public class ServicioProdV2Impl implements ServicioProdV2{

    @Override
    public Set<Producto> obtenerTodosLosProductos() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'obtenerTodosLosProductos'");
    }

    @Override
    public List<String> obtenerTodosLosTiposProductos() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'obtenerTodosLosTiposProductos'");
    }

}

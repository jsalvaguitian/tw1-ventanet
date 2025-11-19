package com.tallerwebi.dominio.servicios;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tallerwebi.dominio.entidades.MedioDePago;
import com.tallerwebi.dominio.repositorios_interfaces.RepositorioMedioDePago;

@Service
@Transactional
public class ServicioMedioDePagoImpl implements ServicioMedioDePago{
    private final RepositorioMedioDePago mediodePagoRepository;

     @Autowired
    public ServicioMedioDePagoImpl(RepositorioMedioDePago mediodePagoRepository) {
        this.mediodePagoRepository = mediodePagoRepository;        
    }

    @Override
    public List<MedioDePago> obtenerTodosLosMedios() {
        return mediodePagoRepository.obtenerTodos();
    }

    @Override
    public MedioDePago obtenerPorId(Long id) {
        return mediodePagoRepository.buscarPorId(id);
    }

}

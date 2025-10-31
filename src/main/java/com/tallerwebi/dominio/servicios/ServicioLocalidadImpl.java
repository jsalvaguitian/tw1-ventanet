package com.tallerwebi.dominio.servicios;

import java.util.List;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;

import com.tallerwebi.dominio.entidades.Localidad;
import com.tallerwebi.dominio.excepcion.LocalidadesExceptions;
import com.tallerwebi.infraestructura.RepositorioLocalidadImpl;

@Service("servicioLocalidad")
@Transactional
public class ServicioLocalidadImpl implements ServicioLocalidad {
    private final RepositorioLocalidadImpl localidadRepository;

    public ServicioLocalidadImpl(RepositorioLocalidadImpl localidadRepository) {
        this.localidadRepository = localidadRepository;
    }

    @Override
    public void actualizar(Localidad localidad) {
        Localidad localidadObt = localidadRepository.obtener(localidad.getId_localidad());
        if (localidadObt == null) {
            throw new LocalidadesExceptions.NoHayLocalidadExistente(
                    "No existe la localidad con id: " + localidad.getId_localidad());
        }
        localidadRepository.actualizar(localidad);
    }

    @Override
    public void eliminar(Long id) {
        Localidad localidadObt = localidadRepository.obtener(id);
        if (localidadObt == null) {
            throw new LocalidadesExceptions.NoHayLocalidadExistente("No existe la localidad con id: " + id);
        }
        localidadRepository.eliminar(id);
    }

    @Override
    public List<Localidad> obtener() {
        return this.localidadRepository.obtener();
    }

    @Override
    public void crearLocalidad(Localidad localidad) {
        this.localidadRepository.guardar(localidad);
    }

    @Override
    public Localidad obtenerPorId(Long id) {
        Localidad localidad = localidadRepository.obtener(id);
        if (localidad == null) {
            throw new LocalidadesExceptions.NoHayLocalidadExistente("No existe la localidad con id: " + id);
        }
        return localidad;
    }
}

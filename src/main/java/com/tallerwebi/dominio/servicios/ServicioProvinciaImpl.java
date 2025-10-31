package com.tallerwebi.dominio.servicios;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;
import com.tallerwebi.dominio.entidades.Provincia;
import com.tallerwebi.dominio.excepcion.ProvinciasExceptions;
import com.tallerwebi.infraestructura.RepositorioProvinciaImpl;

@Service("servicioProvincia")
@Transactional
public class ServicioProvinciaImpl implements ServicioProvincia{
private final RepositorioProvinciaImpl  provinciaRepository;

    public ServicioProvinciaImpl(RepositorioProvinciaImpl provinciaRepository) {
        this.provinciaRepository = provinciaRepository;
    }

    @Override
    public void actualizar(Provincia provincia) {
        Provincia provinciaObt = provinciaRepository.obtener(provincia.getId_provincia());
        if (provinciaObt == null) {
            throw new ProvinciasExceptions.NoHayProvinciaExistente("No existe la provincia con id: " + provincia.getId_provincia());
        }
        provinciaRepository.actualizar(provincia);
    }

    @Override
    public void eliminar(Long id) {
        Provincia provinciaObt = provinciaRepository.obtener(id);
        if (provinciaObt == null) {
            throw new ProvinciasExceptions.NoHayProvinciaExistente("No existe la provincia con id: " + id);
        }
        provinciaRepository.eliminar(id);
    }

    @Override
    public List<Provincia> obtener() {
        return this.provinciaRepository.obtener();
    }

    @Override
    public void crearProvincia(Provincia provincia) {
        this.provinciaRepository.guardar(provincia);
    }

    @Override
    public Provincia obtenerPorId(Long id) {
        Provincia provincia = provinciaRepository.obtener(id);
        if (provincia == null) {
            throw new ProvinciasExceptions.NoHayProvinciaExistente("No existe la provincia con id: " + id);
        }
        return provincia;
    }
}

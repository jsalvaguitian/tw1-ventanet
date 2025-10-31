package com.tallerwebi.dominio.servicios;

import java.util.List;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;
import com.tallerwebi.dominio.entidades.Partido;
import com.tallerwebi.dominio.excepcion.PartidosExceptions;
import com.tallerwebi.infraestructura.RepositorioPartidoImpl;

@Service("servicioPartido")
@Transactional
public class ServicioPartidoImpl implements ServicioPartido {
    private final RepositorioPartidoImpl partidoRepository;

    public ServicioPartidoImpl(RepositorioPartidoImpl partidoRepository) {
        this.partidoRepository = partidoRepository;
    }

    @Override
    public void actualizar(Partido partido) {
        Partido partidoObt = partidoRepository.obtener(partido.getId_partido());
        if (partidoObt == null) {
            throw new PartidosExceptions.NoHayPartidoExistente(
                    "No existe el partido con id: " + partido.getId_partido());
        }
        partidoRepository.actualizar(partido);
    }

    @Override
    public void eliminar(Long id) {
        Partido partidoObt = partidoRepository.obtener(id);
        if (partidoObt == null) {
            throw new PartidosExceptions.NoHayPartidoExistente("No existe el partido con id: " + id);
        }
        partidoRepository.eliminar(id);
    }

    @Override
    public List<Partido> obtener() {
        return this.partidoRepository.obtener();
    }

    @Override
    public void crearPartido(Partido partido) {
        this.partidoRepository.guardar(partido);
    }

    @Override
    public Partido obtenerPorId(Long id) {
        Partido partido = partidoRepository.obtener(id);
        if (partido == null) {
            throw new PartidosExceptions.NoHayPartidoExistente("No existe el partido con id: " + id);
        }
        return partido;
    }
}

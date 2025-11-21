package com.tallerwebi.dominio.servicios;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.tallerwebi.dominio.entidades.Alto;
import com.tallerwebi.dominio.entidades.Ancho;
import com.tallerwebi.dominio.entidades.Color;
import com.tallerwebi.dominio.entidades.Localidad;
import com.tallerwebi.dominio.entidades.MaterialDePerfil;
import com.tallerwebi.dominio.entidades.Partido;
import com.tallerwebi.dominio.entidades.Provincia;
import com.tallerwebi.dominio.entidades.TipoDeVidrio;
import com.tallerwebi.dominio.repositorios_interfaces.RepositorioTipoDeVidrio;
import com.tallerwebi.infraestructura.RepositorioAltoImpl;
import com.tallerwebi.infraestructura.RepositorioAnchoImpl;
import com.tallerwebi.infraestructura.RepositorioColorImpl;
import com.tallerwebi.infraestructura.RepositorioLocalidadImpl;
import com.tallerwebi.infraestructura.RepositorioMaterialImpl;
import com.tallerwebi.infraestructura.RepositorioPartidoImpl;
import com.tallerwebi.infraestructura.RepositorioProvinciaImpl;

@Service("servicioTablas")
@Transactional
public class ServicioTablasImpl implements ServicioTablas {
    private final RepositorioColorImpl colorRepository;
    private final RepositorioMaterialImpl materialRepository;
    private final RepositorioAltoImpl altoRepository;
    private final RepositorioAnchoImpl anchoRepository;
    private final RepositorioProvinciaImpl provinciaRepository;
    private final RepositorioLocalidadImpl localidadRepository;
    private final RepositorioPartidoImpl partidoRepository;
    private final RepositorioTipoDeVidrio tipoVidrioRepository;

    public ServicioTablasImpl(RepositorioColorImpl colorRepository, RepositorioMaterialImpl materialRepository,
            RepositorioAltoImpl altoRepository, RepositorioAnchoImpl anchoRepository,
            RepositorioProvinciaImpl provinciaRepository, RepositorioLocalidadImpl localidadRepository,
            RepositorioPartidoImpl partidoRepository, RepositorioTipoDeVidrio tipoVidrioRepository) {
        this.colorRepository = colorRepository;
        this.materialRepository = materialRepository;
        this.altoRepository = altoRepository;
        this.anchoRepository = anchoRepository;
        this.provinciaRepository = provinciaRepository;
        this.localidadRepository = localidadRepository;
        this.partidoRepository = partidoRepository;
        this.tipoVidrioRepository = tipoVidrioRepository;
    }

    @Override
    public List<Color> obtenerColores() {
        return this.colorRepository.obtener();
    }

    @Override
    public List<MaterialDePerfil> obtenerMateriales() {
        return this.materialRepository.obtener();
    }

    @Override
    public List<Alto> obtenerAltos() {
        return this.altoRepository.obtener();
    }

    @Override
    public List<Ancho> obtenerAnchos() {
        return this.anchoRepository.obtener();
    }

    @Override
    public List<Provincia> obtenerProvincias() {
        return this.provinciaRepository.obtener();
    }

    @Override
    public List<Localidad> obtenerLocalidadesPorProvincia(Long provinciaId) {
        return this.localidadRepository.obtenerPorIdDeProvincia(provinciaId);
    }

    @Override
    public List<Partido> obtenerPartidosPorLocalidad(Long localidadId) {
        return this.partidoRepository.obtenerPorIdDeLocalidad(localidadId);
    }

    @Override
    public Alto obtenerAltoPorId(Long id) {
        return this.altoRepository.obtener(id);
    }

    @Override
    public Ancho obtenerAnchoPorId(Long id) {
        return this.anchoRepository.obtener(id);
    }

    @Override
    public MaterialDePerfil obtenerMaterialPorId(Long id) {
        return this.materialRepository.obtener(id);    
    }

    @Override
    public Color obtenerColorPorId(Long id) {
        return this.colorRepository.obtener(id);
    }

    @Override
    public TipoDeVidrio obtenerTipoDeVidrioPorId(Long id) {
        return this.tipoVidrioRepository.buscarPorId(id);
    }
}
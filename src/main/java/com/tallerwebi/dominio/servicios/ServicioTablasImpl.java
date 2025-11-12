package com.tallerwebi.dominio.servicios;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.tallerwebi.dominio.entidades.Alto;
import com.tallerwebi.dominio.entidades.Ancho;
import com.tallerwebi.dominio.entidades.Color;
import com.tallerwebi.dominio.entidades.Localidad;
import com.tallerwebi.dominio.entidades.Marca;
import com.tallerwebi.dominio.entidades.Material;
import com.tallerwebi.dominio.entidades.Partido;
import com.tallerwebi.dominio.entidades.Presentacion;
import com.tallerwebi.dominio.entidades.Provincia;
import com.tallerwebi.dominio.entidades.SubTipoProducto;
import com.tallerwebi.dominio.entidades.TipoDeVidrio;
import com.tallerwebi.dominio.entidades.TipoProducto;
import com.tallerwebi.dominio.repositorios_interfaces.RepositorioTipoDeVidrio;
import com.tallerwebi.infraestructura.RepositorioAltoImpl;
import com.tallerwebi.infraestructura.RepositorioAnchoImpl;
import com.tallerwebi.infraestructura.RepositorioColorImpl;
import com.tallerwebi.infraestructura.RepositorioLocalidadImpl;
import com.tallerwebi.infraestructura.RepositorioMarcaImpl;
import com.tallerwebi.infraestructura.RepositorioMaterialImpl;
import com.tallerwebi.infraestructura.RepositorioPartidoImpl;
import com.tallerwebi.infraestructura.RepositorioPresentacionImpl;
import com.tallerwebi.infraestructura.RepositorioProvinciaImpl;
import com.tallerwebi.infraestructura.RepositorioSubTipoProductoImpl;
import com.tallerwebi.infraestructura.RepositorioTipoProductoImpl;

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
    private final RepositorioMarcaImpl marcaRepository;
    private final RepositorioPresentacionImpl representacionRepository;
    private final RepositorioSubTipoProductoImpl subTipoProductoRepo;
    private final RepositorioTipoDeVidrio tipoDeVidrioRepo;
    private final RepositorioTipoProductoImpl tipoProductoRepo;

    public ServicioTablasImpl(RepositorioColorImpl colorRepository, RepositorioMaterialImpl materialRepository,
            RepositorioAltoImpl altoRepository, RepositorioAnchoImpl anchoRepository,
            RepositorioProvinciaImpl provinciaRepository, RepositorioLocalidadImpl localidadRepository,
            RepositorioPartidoImpl partidoRepository, RepositorioMarcaImpl repositorioMarca, 
            RepositorioPresentacionImpl repositorioPresentacion, RepositorioSubTipoProductoImpl repositorioSubTipoProducto,
            RepositorioTipoDeVidrio repositorioTipoDeVidrio, RepositorioTipoProductoImpl repositorioTipoProducto) {
        this.colorRepository = colorRepository;
        this.materialRepository = materialRepository;
        this.altoRepository = altoRepository;
        this.anchoRepository = anchoRepository;
        this.provinciaRepository = provinciaRepository;
        this.localidadRepository = localidadRepository;
        this.partidoRepository = partidoRepository;
        this.marcaRepository = repositorioMarca;
        this.representacionRepository = repositorioPresentacion;
        this.subTipoProductoRepo = repositorioSubTipoProducto;
        this.tipoDeVidrioRepo = repositorioTipoDeVidrio;
        this.tipoProductoRepo = repositorioTipoProducto;
    }

    @Override
    public List<Color> obtenerColores() {
        return this.colorRepository.obtener();
    }

    @Override
    public List<Material> obtenerMateriales() {
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
    public List<Marca> obtenerMarcas() {
        return this.marcaRepository.obtener();
    }

    @Override
    public List<Presentacion> obtenePresentaciones() {
        return this.representacionRepository.obtener();
    }

    @Override
    public List<SubTipoProducto> obteneSubTipoProductos() {
        return this.subTipoProductoRepo.obtener();
    }

    @Override
    public List<TipoDeVidrio> obtenerTipoDeVidrios() {
        return this.tipoDeVidrioRepo.obtener();
    }

    @Override
    public List<TipoProducto> obtenerTipoProductos() {
        return this.tipoProductoRepo.obtener();
    }

    
}
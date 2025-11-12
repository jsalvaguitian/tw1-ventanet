package com.tallerwebi.dominio.servicios;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tallerwebi.dominio.entidades.Alto;
import com.tallerwebi.dominio.entidades.Ancho;
import com.tallerwebi.dominio.entidades.Color;
import com.tallerwebi.dominio.entidades.Marca;
import com.tallerwebi.dominio.entidades.Material;
import com.tallerwebi.dominio.entidades.Presentacion;
import com.tallerwebi.dominio.entidades.Producto;
import com.tallerwebi.dominio.entidades.SubTipoProducto;
import com.tallerwebi.dominio.entidades.TipoDeVidrio;
import com.tallerwebi.dominio.entidades.TipoProducto;
import com.tallerwebi.dominio.excepcion.CloudinaryException;
import com.tallerwebi.dominio.excepcion.ExcelReaderException;
import com.tallerwebi.dominio.repositorios_interfaces.RepositorioProveedor;
import com.tallerwebi.dominio.repositorios_interfaces.RepositorioTipoDeVidrio;
import com.tallerwebi.dominio.utils.ExcelReader;
import com.tallerwebi.infraestructura.RepositorioAltoImpl;
import com.tallerwebi.infraestructura.RepositorioAnchoImpl;
import com.tallerwebi.infraestructura.RepositorioColorImpl;
import com.tallerwebi.infraestructura.RepositorioMarcaImpl;
import com.tallerwebi.infraestructura.RepositorioMaterialImpl;
import com.tallerwebi.infraestructura.RepositorioPresentacionImpl;
import com.tallerwebi.infraestructura.RepositorioProductoImpl;
import com.tallerwebi.infraestructura.RepositorioSubTipoProductoImpl;
import com.tallerwebi.infraestructura.RepositorioTipoProductoImpl;
import com.tallerwebi.presentacion.dto.ProductoImportDTO;

@Service
@Transactional
public class ServicioImportacionProductImpl implements ServicioImportacionProduct {

    private ServicioCloudinary servicioCloudinary;
    private RepositorioProductoImpl repositorioProducto;

    private RepositorioAltoImpl repositorioAltoImpl;
    private RepositorioAnchoImpl repositorioAnchoImpl;
    private RepositorioColorImpl repositorioColorImpl;
    private RepositorioMarcaImpl repositorioMarcaImpl;
    private RepositorioMaterialImpl repositorioMaterialImpl;
    private RepositorioPresentacionImpl repositorioPresentacionImpl;
    private RepositorioSubTipoProductoImpl repositorioSubTipoProductoImpl;
    private RepositorioTipoProductoImpl repositorioTipoProductoImpl;
    private RepositorioTipoDeVidrio repositorioTipoDeVidrio;
    private RepositorioProveedor repositorioProveedor;

    @Autowired
    public ServicioImportacionProductImpl(RepositorioAltoImpl repositorioAltoImpl,
            RepositorioAnchoImpl repositorioAnchoImpl, RepositorioColorImpl repositorioColorImpll,
            RepositorioMarcaImpl repositorioMarcaImpl, RepositorioMaterialImpl repositorioMaterialImpl,
            RepositorioPresentacionImpl repositorioPresentacionImpl,
            RepositorioSubTipoProductoImpl repositorioSubTipoProductoImpl,
            RepositorioTipoProductoImpl repositorioTipoProductoImpl, RepositorioTipoDeVidrio repositorioTipoDeVidrio,
            RepositorioProductoImpl productoRepo,
            ServicioCloudinary servicioCloudinary, RepositorioProveedor repositorioProveedor) {

        this.repositorioAltoImpl = repositorioAltoImpl;
        this.repositorioAnchoImpl = repositorioAnchoImpl;
        this.repositorioColorImpl = repositorioColorImpll;
        this.repositorioMarcaImpl = repositorioMarcaImpl;
        this.repositorioMaterialImpl = repositorioMaterialImpl;
        this.repositorioPresentacionImpl = repositorioPresentacionImpl;
        this.repositorioSubTipoProductoImpl = repositorioSubTipoProductoImpl;
        this.repositorioTipoProductoImpl = repositorioTipoProductoImpl;
        this.repositorioTipoDeVidrio = repositorioTipoDeVidrio;
        this.repositorioProducto = productoRepo;
        this.servicioCloudinary = servicioCloudinary;
        this.repositorioProveedor = repositorioProveedor;
    }

    @Override
    public List<ProductoImportDTO> leerYValidar(MultipartFile archivoExcel, List<MultipartFile> imagenes)
            throws ExcelReaderException {

        List<ProductoImportDTO> productos = ExcelReader.leerProductos(archivoExcel);

        validarReferencias(productos, imagenes);

        return productos;

    }

    private void validarReferencias(List<ProductoImportDTO> productos, List<MultipartFile> imagenes) {
        StringBuilder errores = new StringBuilder(); // Para concatenar string
        StringBuilder advertencias = new StringBuilder();

        // Normalizar nombres de imágenes subidas (sin extensión, todo minúscula)
        Set<String> nombresImagenesNormalizados = new HashSet<>();
        List<String> duplicadas = new ArrayList<>();

        for (MultipartFile img : imagenes) {
            String nombre = img.getOriginalFilename();
            if (nombre == null || nombre.trim().isEmpty()) {
                continue;
            }

            int punto = nombre.lastIndexOf(".");
            if (punto > 0) {
                nombre = nombre.substring(0, punto); // sin extensión
            }

            nombre = nombre.trim().toLowerCase();

            // Verificar duplicados
            if (!nombresImagenesNormalizados.add(nombre)) {
                duplicadas.add(nombre);
            }
        }

        // Si hay duplicadas, lanzar excepción o registrar error
        if (!duplicadas.isEmpty()) {
            errores.append("Error: Imágenes duplicadas -> " + duplicadas);
        }

        for (ProductoImportDTO dtoProduct : productos) {

            if (repositorioProducto.buscarPorNombre(dtoProduct.getNombre()) != null) {
                errores.append("El producto con nombre: '" + dtoProduct.getNombre() + "' ya existe");
                continue;
            }

            if (repositorioAltoImpl.buscarPorNombre(dtoProduct.getAlto()) == null)
                advertencias.append("Alto no existe. ");

            if (repositorioAnchoImpl.buscarPorNombre(dtoProduct.getAncho()) == null)
                advertencias.append("Ancho no existe. ");

            if (repositorioColorImpl.buscarPorNombre(dtoProduct.getColor()) == null)
                advertencias.append("Color no existe. ");

            if (repositorioMarcaImpl.buscarPorNombre(dtoProduct.getMarca()) == null)
                advertencias.append("La marca no existe. ");

            if (repositorioMaterialImpl.buscarPorNombre(dtoProduct.getMaterial()) == null)
                advertencias.append("El material no existe. ");

            if (repositorioPresentacionImpl.buscarPorNombre(dtoProduct.getPresentacion()) == null)
                errores.append("La presentacion no existe. ");

            TipoProducto tipo = repositorioTipoProductoImpl.buscarPorNombre(dtoProduct.getTipoProducto());
            if (tipo == null) {
                advertencias.append("El tipo de producto no existe. ");

            } else {
                SubTipoProducto subTipo = repositorioSubTipoProductoImpl
                        .buscarPorNombreYTipo(dtoProduct.getSubtipoProducto(), tipo.getId());
                if (subTipo == null) {
                    advertencias.append("El subtipo '" + dtoProduct.getSubtipoProducto()
                            + "' no existe para el tipo '" + tipo.getNombre() + "'. ");
                }
            }

            if (repositorioTipoDeVidrio.buscarPorNombre(dtoProduct.getTipoVidrio()) == null)
                advertencias.append("El tipo de vidrio no existe. ");

            if (dtoProduct.getPrecio() <= 0)
                errores.append("El precio no tiene que ser un numero menor o igual cero. ");

            if (dtoProduct.getStock() <= 0)
                errores.append("El stock no tiene que ser un numero menor o igual cero. ");

            if (dtoProduct.getNombreImagen() == null || dtoProduct.getNombreImagen().trim().isEmpty()) {
                errores.append("El nombre de la imagen está vacío. ");
            } else {
                String nombreExcel = dtoProduct.getNombreImagen().trim();
                int punto = nombreExcel.lastIndexOf(".");
                if (punto > 0)
                    nombreExcel = nombreExcel.substring(0, punto); // sin extensión
                nombreExcel = nombreExcel.toLowerCase();

                if (!nombresImagenesNormalizados.contains(nombreExcel)) {
                    errores.append("La imagen '" + dtoProduct.getNombreImagen()
                            + "' no fue cargada o no coincide con el archivo subido. ");
                }
            }

            if (errores.length() > 0) {
                dtoProduct.setValido(false);
                dtoProduct.setError(errores.toString());
                dtoProduct.setAdvertencia(advertencias.toString());
            } else {
                dtoProduct.setValido(true);
                dtoProduct.setAdvertencia(advertencias.toString());
            }
        }
    }

    public void importar(List<ProductoImportDTO> productos, List<File> archivosLocales, Long proveedorID)
            throws CloudinaryException, IOException {

        for (ProductoImportDTO dto : productos) {

            if (!dto.getValido())
                continue; // q pase el siguiente bucle, no lo guardo
            Producto producto = new Producto();

            producto.setNombre(dto.getNombre());
            producto.setDescripcion(dto.getDescripcion());
            producto.setPrecio(dto.getPrecio());
            producto.setStock(dto.getStock());

            Marca marca = repositorioMarcaImpl.buscarPorNombre(dto.getMarca());
            if (marca == null) {
                marca = new Marca(dto.getMarca());
                repositorioMarcaImpl.guardar(marca); // creo la marca q no existe
            }
            producto.setMarca(marca);

            Alto alto = repositorioAltoImpl.buscarPorNombre(dto.getAlto());
            if (alto == null) {
                alto = new Alto(dto.getAlto());
                repositorioAltoImpl.guardar(alto);
            }
            producto.setAlto(alto);

            Ancho ancho = repositorioAnchoImpl.buscarPorNombre(dto.getAncho());
            if (ancho == null) {
                ancho = new Ancho(dto.getAncho());
                repositorioAnchoImpl.guardar(ancho);
            }
            producto.setAncho(ancho);

            Color color = repositorioColorImpl.buscarPorNombre(dto.getColor());
            if (color == null) {
                color = new Color(dto.getColor());
                repositorioColorImpl.guardar(color);
            }
            producto.setColor(color);

            Material material = repositorioMaterialImpl.buscarPorNombre(dto.getMaterial());
            if (material == null) {
                material = new Material(dto.getMaterial());
                repositorioMaterialImpl.guardar(material);
            }
            producto.setMaterial(material);

            Presentacion presentacion = repositorioPresentacionImpl.buscarPorNombre(dto.getPresentacion());

            producto.setPresentacion(presentacion);

            TipoProducto tipoProducto = repositorioTipoProductoImpl.buscarPorNombre(dto.getTipoProducto());
            if (tipoProducto == null) {
                tipoProducto = new TipoProducto(dto.getTipoProducto());
                repositorioTipoProductoImpl.guardar(tipoProducto);
            }
            producto.setTipoProducto(tipoProducto);

            SubTipoProducto subTipoProducto = repositorioSubTipoProductoImpl.buscarPorNombreYTipo(
                    dto.getSubtipoProducto(), tipoProducto.getId());

            if (subTipoProducto == null) {
                subTipoProducto = new SubTipoProducto(dto.getSubtipoProducto());
                subTipoProducto.setTipoProducto(tipoProducto); // relaciono el tipo producto con el subtipo
                repositorioSubTipoProductoImpl.guardar(subTipoProducto);
            }
            producto.setSubTipoProducto(subTipoProducto);

            TipoDeVidrio tipoDeVidrio = repositorioTipoDeVidrio.buscarPorNombre(dto.getTipoVidrio());
            if (tipoDeVidrio == null) {
                tipoDeVidrio = new TipoDeVidrio(dto.getTipoVidrio());
                repositorioTipoDeVidrio.guardar(tipoDeVidrio);

            }
            producto.setTipoDeVidrio(tipoDeVidrio);

            // -------------------------------------

            // guardo la imagen en cloudinary
            File imagen = buscarImagenLocalPorNombre(dto.getNombreImagen(), archivosLocales);
            if (imagen == null) {
                dto.setError("No se encontro la imagen correspondiente, no se importara este producto.");
                continue;
            }

            Map<String, Object> resultado = servicioCloudinary.subirImagen(imagen);

            producto.setImagenUrl((String) resultado.get("url"));
            producto.setImgCloudinaryID((String) resultado.get("public_id"));

            producto.setProveedor(repositorioProveedor.buscarPorId(proveedorID));// ASOCIO EL PRODUCTO CON EL PROVEEDOR

            repositorioProducto.guardar(producto);
        }
    }

    private File buscarImagenLocalPorNombre(String nombreBuscado, List<File> archivos) {
        if (nombreBuscado == null)
            return null;

        // Quitar extensión y normalizar (por si viene con .jpg, .jpeg, etc)
        int punto = nombreBuscado.lastIndexOf(".");
        if (punto > 0)
            nombreBuscado = nombreBuscado.substring(0, punto);
        String normalizado = nombreBuscado.trim().toLowerCase();

        for (File file : archivos) {
            String nombre = file.getName();
            int p = nombre.lastIndexOf(".");
            if (p > 0)
                nombre = nombre.substring(0, p);
            if (nombre.trim().toLowerCase().equals(normalizado)) {
                return file;
            }
        }
        return null;
    }
}

/*
 * private MultipartFile buscarImagenPorNombre(String nombreBuscado,
 * List<MultipartFile> imagenes) {
 * if (nombreBuscado == null)
 * return null;
 * 
 * String normalizado = nombreBuscado.trim().toLowerCase();
 * for (MultipartFile img : imagenes) {
 * String nombre = img.getOriginalFilename();
 * if (nombre != null) {
 * int punto = nombre.lastIndexOf(".");
 * if (punto > 0)
 * nombre = nombre.substring(0, punto);
 * if (nombre.trim().toLowerCase().equals(normalizado)) {
 * return img;
 * }
 * }
 * }
 * return null;
 * }
 */

/*
 * private Producto mapearProducto(ProductoImportDTO dto) {
 * Producto producto = new Producto();
 * producto.setNombre(dto.getNombre());
 * producto.setDescripcion(dto.getDescripcion());
 * producto.setPrecio(dto.getPrecio());
 * producto.setStock(dto.getStock());
 * producto.setImagenUrl(dto.getUrlImagen());
 * producto.setImgCloudinaryID(dto.getImgCloudinaryId());
 * 
 * repositorioAltoImpl.buscarPorNombre(dto.getAlto());
 * repositorioAnchoImpl.buscarPorNombre(dto.getAncho());
 * 
 * 
 * producto.setMarca(marcaRepositorio.findByNombre(dto.getMarca()));
 * producto.setPresentacion(presentacionRepositorio.findByDescripcion(dto.
 * getPresentacion()));
 * 
 * return producto;
 * }
 */

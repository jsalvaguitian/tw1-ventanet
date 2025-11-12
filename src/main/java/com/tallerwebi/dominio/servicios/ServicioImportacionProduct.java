package com.tallerwebi.dominio.servicios;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.tallerwebi.dominio.excepcion.CloudinaryException;
import com.tallerwebi.dominio.excepcion.ExcelReaderException;
import com.tallerwebi.presentacion.dto.ProductoImportDTO;

public interface ServicioImportacionProduct {

    List<ProductoImportDTO> leerYValidar(MultipartFile archivoExcel, List<MultipartFile> imagenes) throws ExcelReaderException;
    
    void importar(List<ProductoImportDTO> productos, List<File> archivosLocales, Long proveedorId) throws CloudinaryException, IOException;


}

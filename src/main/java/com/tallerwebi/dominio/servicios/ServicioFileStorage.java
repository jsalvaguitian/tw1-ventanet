package com.tallerwebi.dominio.servicios;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ServicioFileStorage {

    private final Path UPLOAD_DIR = Paths.get(System.getProperty("user.dir"),
            "src", "main", "webapp", "resources", "core", "uploads");
    private final long MAX_FILE_SIZE; // 3 MB

    public ServicioFileStorage() {
        this.MAX_FILE_SIZE = 3 * 1024 * 1024;
    }

    public ServicioFileStorage(long maxFileSize) {
        this.MAX_FILE_SIZE = maxFileSize;
    }

    private String guardarArchivoGenerico(MultipartFile archivo,
                                          List<String> extensionesValidas,
                                          List<String> tiposMimeValidos,
                                          String subcarpeta) throws IOException {

        if (archivo.isEmpty())
            throw new IOException("El archivo es obligatorio.");

        if (archivo.getSize() > MAX_FILE_SIZE)
            throw new IOException("El archivo debe ser menor a " + (MAX_FILE_SIZE / (1024 * 1024)) + " MB");

        String nombreArchivo = archivo.getOriginalFilename();
        if (nombreArchivo == null)
            throw new IOException("El archivo no tiene nombre.");

        // Verificar extensión
        boolean extensionValida = extensionesValidas.stream().anyMatch(nombreArchivo::endsWith);
        if (!extensionValida)
            throw new IOException("Extensión no permitida.");

        // Verificar content type
        String contentType = archivo.getContentType();
        boolean tipoValido = contentType != null && tiposMimeValidos.contains(contentType);
        if (!tipoValido)
            throw new IOException("Tipo de archivo no permitido: " + contentType);

        // Crear carpeta destino si no existe (ej: /uploads/afip o /uploads/varios)
        Path carpetaDestino = UPLOAD_DIR.resolve(subcarpeta);
        if (!Files.exists(carpetaDestino))
            Files.createDirectories(carpetaDestino);

        // Generar nombre único y guardar archivo
        String nombreUnico = UUID.randomUUID() + "_" + nombreArchivo;
        Path destino = carpetaDestino.resolve(nombreUnico);
        Files.copy(archivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

        // Ruta relativa para guardar en BD
        return "/resources/core/uploads/" + subcarpeta + "/" + nombreUnico;
    }

    public String guardarArchivoImgOPdf(MultipartFile archivo) throws IOException {
        return guardarArchivoGenerico(archivo,
                List.of(".pdf", ".jpg", ".jpeg", ".png"),
                List.of("application/pdf", "image/jpeg", "image/png"),
                "afip");
    }

    public String guardarArchivoDocumento(MultipartFile archivo) throws IOException {
        return guardarArchivoGenerico(archivo,
                List.of(".pdf", ".xls", ".xlsx", ".csv", ".ods"),
                List.of(
                        "application/pdf",
                        "application/vnd.ms-excel",
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                        "text/csv",
                        "application/vnd.oasis.opendocument.spreadsheet"
                ),
                "varios");
    }
}
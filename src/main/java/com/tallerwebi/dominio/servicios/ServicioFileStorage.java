package com.tallerwebi.dominio.servicios;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ServicioFileStorage {

    private final String UPLOAD_DIR = "src/main/webapp/uploads/";
    private final long MAX_FILE_SIZE = 3 * 1024 * 1024; // 3MB te permito por ahora

    public String guardarArchivo(MultipartFile documento) throws IOException {
        // tambien valido aqui si esta vacio
        if (documento.isEmpty())
            throw new IOException("El documento es obligatorio");

        // validacion del tamanio
        if (documento.getSize() > MAX_FILE_SIZE) {
            throw new IOException("El documento debe ser menor a 3MB");
        }

        // validacion del tipo de archivo (no queremos .exe jaja)
        String nombreArchivo = documento.getOriginalFilename();

        if (nombreArchivo == null || !(nombreArchivo.endsWith(".pdf") || nombreArchivo.endsWith(".jpg")
                || nombreArchivo.endsWith(".png") || nombreArchivo.endsWith(".jpeg")))
            throw new IOException("Solo se aceptan archivos pdf, jpg, png, jpeg");

        String contentType = documento.getContentType();
        if (contentType == null || !(contentType.equals("application/pdf") || contentType.equals("image/jpeg")
                || contentType.equals("image/png"))) {
            throw new IOException("Tipo de archivo no permitido");
        }

        // creo la carpeta si no existe
        Files.createDirectories(Paths.get(UPLOAD_DIR));

        // creo un nombre unico para evitar sobreescrituras
        String nombreUnico = UUID.randomUUID() + "_" + nombreArchivo;
        Path destinoDocu = Paths.get(UPLOAD_DIR + nombreUnico);

        Files.copy(documento.getInputStream(), destinoDocu, StandardCopyOption.REPLACE_EXISTING);

        return "/uploads/" + nombreUnico; // retorno el path relativo para guardarlo en la BD
    }

}

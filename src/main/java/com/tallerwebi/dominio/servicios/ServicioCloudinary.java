package com.tallerwebi.dominio.servicios;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Service
public class ServicioCloudinary {

    private final Cloudinary cloudinary;

    @Autowired
    public ServicioCloudinary(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public Map<String, Object> subirImagen(MultipartFile imagen) throws IOException{
        Map uploadResult = cloudinary.uploader().upload(imagen.getBytes(), ObjectUtils.emptyMap());
        return uploadResult;
    }

    public void eliminarImagen(String publicId) throws IOException{
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }


    



}

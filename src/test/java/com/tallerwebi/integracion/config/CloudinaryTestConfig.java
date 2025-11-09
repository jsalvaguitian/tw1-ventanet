package com.tallerwebi.integracion.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;

@Configuration
public class CloudinaryTestConfig {
     @Bean
        public Cloudinary cloudinaryMock() {
            // Bean simulado, para que no falle el contexto de Spring
            return new Cloudinary();
        }

}

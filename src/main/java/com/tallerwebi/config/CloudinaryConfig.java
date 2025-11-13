package com.tallerwebi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import io.github.cdimascio.dotenv.Dotenv;

@Configuration
public class CloudinaryConfig {

    Dotenv dotenv = Dotenv.configure()
            .filename(".env.cloudinary")
            .ignoreIfMissing() // no falla si no existe
            .load();

    @Bean
    public Cloudinary cloudinary() {

        String cloudName = dotenv.get("CLOUDINARY_CLOUD_NAME");

        String apiKey = dotenv.get("CLOUDINARY_API_KEY");

        String apiSecret = dotenv.get("CLOUDINARY_API_SECRET");

        if (cloudName == null || apiKey == null || apiSecret == null) {
            throw new IllegalStateException("Faltan variables de entorno para Cloudinary API");
        }

        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret));

    }

}

package com.tallerwebi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.tallerwebi.dominio.utils.ExcelGenerator;

@Configuration
public class ExcelConfig {

    @Bean
    public ExcelGenerator excelGenerator() {
        return new ExcelGenerator();
    }

}

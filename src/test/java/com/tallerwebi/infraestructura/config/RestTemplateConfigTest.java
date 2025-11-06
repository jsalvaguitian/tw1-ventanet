package com.tallerwebi.infraestructura.config;

import com.tallerwebi.config.RestTemplateConfig;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

public class RestTemplateConfigTest {
    
    @Test
    public void deberiaCrearBeanRestTemplate() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(RestTemplateConfig.class);
        RestTemplate restTemplate = context.getBean(RestTemplate.class);
        assertNotNull(restTemplate, "El bean RestTemplate debería haberse creado correctamente");
        context.close();
    }
}
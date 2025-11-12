package com.tallerwebi;

import com.tallerwebi.config.DatabaseInitializationConfig;
import com.tallerwebi.config.HibernateConfig;
import com.tallerwebi.config.SpringWebConfig;

import javax.servlet.Filter;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletRegistration;

import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class MyServletInitializer
        extends AbstractAnnotationConfigDispatcherServletInitializer {

    // services and data sources
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[0];
    }

    // controller, view resolver, handler mapping
    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[] { SpringWebConfig.class, HibernateConfig.class, DatabaseInitializationConfig.class };
    }

    @Override
    protected String[] getServletMappings() {
        return new String[] { "/" };
    }

    @Override
    protected Filter[] getServletFilters() {
        // Creamos y configuramos el filtro para asegurar que todo el tráfico
        // (requests y responses) usen UTF-8.
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");
        characterEncodingFilter.setForceEncoding(true);
        
        // Devolvemos el filtro en un array.
        return new Filter[] { characterEncodingFilter };
    }

    @Override
    protected void customizeRegistration(ServletRegistration.Dynamic registration) {
        // Configura el soporte para archivos (multipart)
        String tempDir = System.getProperty("java.io.tmpdir");
        long maxFileSize = 10485760L; // 10 MB
        long maxRequestSize = 10485760L; // 10 MB
        int fileSizeThreshold = 0;

        MultipartConfigElement multipartConfigElement = new MultipartConfigElement(tempDir, maxFileSize, maxRequestSize,
                fileSizeThreshold);

        registration.setMultipartConfig(multipartConfigElement);

        // Deshabilita la reescritura de URL para mantener las sesiones basadas en cookies
        registration.setInitParameter("disableUrlRewriting", "true");

    }
}

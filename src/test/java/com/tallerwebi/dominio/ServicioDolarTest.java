package com.tallerwebi.dominio;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Optional;

import com.tallerwebi.dominio.servicios.ServicioDolar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class ServicioDolarTest {

    private RestTemplate restTemplateMock;
    private ServicioDolar servicioDolar;

    @BeforeEach
    public void setup() {
        restTemplateMock = Mockito.mock(RestTemplate.class);
        servicioDolar = new ServicioDolar(restTemplateMock);
    }

    @Test
    public void fetchAndCache_siLaApiFallaEntoncesLaCacheNoSeActualiza() {
        Mockito.when(restTemplateMock.getForObject(Mockito.anyString(), Mockito.eq(String.class)))
               .thenThrow(new RestClientException("API caída"));

        servicioDolar.fetchAndCache();

        Optional<?> datos = servicioDolar.getDatosOficial();
        assertFalse(datos.isPresent());
    }
}
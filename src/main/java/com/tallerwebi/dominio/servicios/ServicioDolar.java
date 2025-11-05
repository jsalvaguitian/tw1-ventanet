package com.tallerwebi.dominio.servicios;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.Map;
import java.util.HashMap;

@Service
public class ServicioDolar {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // URL directa al oficial
    private static final String API_URL = "https://dolarapi.com/v1/dolares/oficial";

    private Map<String, BigDecimal> cache = new HashMap<>();

    public ServicioDolar(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        fetchAndCache();
    }

    @Scheduled(fixedDelay = 600000) // 10 minutos
    public void fetchAndCache() {
        try {
            String json = restTemplate.getForObject(API_URL, String.class);
            if (json != null) {
                JsonNode node = objectMapper.readTree(json);
                BigDecimal compra = new BigDecimal(node.get("compra").asText());
                BigDecimal venta = new BigDecimal(node.get("venta").asText());

                cache.put("compra", compra);
                cache.put("venta", venta);

                System.out.println("[ServicioDolar] Actualizado: compra $" + compra + " | venta $" + venta);
            }
        } catch (Exception e) {
            System.err.println("[ServicioDolar] Error al actualizar: " + e.getMessage());
        }
    }

    public Optional<Map<String, BigDecimal>> getDatosOficial() {
        return cache.isEmpty() ? Optional.empty() : Optional.of(cache);
    }
}
package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.servicios.ServicioDolar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/dolar")
public class ControladorDolar {

    @Autowired
    private ServicioDolar servicioDolar;

    @GetMapping("/venta")
    public ResponseEntity<BigDecimal> obtenerDolarVenta() {
        Map<String, BigDecimal> datos = servicioDolar.getDatosOficialOrThrow();
        return ResponseEntity.ok(datos.get("venta"));
    }
}

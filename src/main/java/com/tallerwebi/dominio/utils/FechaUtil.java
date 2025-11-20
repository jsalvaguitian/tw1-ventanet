package com.tallerwebi.dominio.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

@Component("fechaUtil")
public class FechaUtil {

    private static final DateTimeFormatter FORMAT_FECHA_HORA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Para LocalDateTime
    public String formatear(LocalDateTime fecha) {
        if (fecha == null) return "-";
        return fecha.format(FORMAT_FECHA_HORA);
    }

    // Para LocalDate
    public String formatear(LocalDate fecha) {
        if (fecha == null) return "-";
        return fecha.atStartOfDay().format(FORMAT_FECHA_HORA);
    }
}
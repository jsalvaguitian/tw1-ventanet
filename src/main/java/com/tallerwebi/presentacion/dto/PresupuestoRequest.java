package com.tallerwebi.presentacion.dto;

import java.util.List;

public class PresupuestoRequest {
    public Long provinciaId;
    public Long localidadId;
    public Long partidoId;
    public List<PresupuestoItemRequest> items;
}

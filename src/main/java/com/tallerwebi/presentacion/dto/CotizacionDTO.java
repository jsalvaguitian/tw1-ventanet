package com.tallerwebi.presentacion.dto;

import java.util.List;

public class CotizacionDTO {
     private Long id;
    private Long proveedorId;
    private Long clienteId;
    
    private Double montoTotal;
    private List<CotizacionItemDTO> items;
    private boolean seleccionada;
    private Long medioPagoId;

    public CotizacionDTO() {}

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Long getMedioPagoId() {
        return medioPagoId;
    }


    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public Long getProveedorId() {
        return proveedorId;
    }

    public void setProveedorId(Long proveedorId) {
        this.proveedorId = proveedorId;
    }

    public Double getMontoTotal() {
        return montoTotal;
    }
    public void setMontoTotal(Double montoTotal) {
        this.montoTotal = montoTotal;
    }

    public List<CotizacionItemDTO> getItems() {
        return items;
    }
    public void setItems(List<CotizacionItemDTO> items) {
        this.items = items;
    }

    public boolean isSeleccionada() {
        return seleccionada;
    }
    public void setSeleccionada(boolean seleccionada) {
        this.seleccionada = seleccionada;
    }

    
}

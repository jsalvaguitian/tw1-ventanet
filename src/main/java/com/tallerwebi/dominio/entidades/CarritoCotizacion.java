package com.tallerwebi.dominio.entidades;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class CarritoCotizacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Cliente cliente;

    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CarritoItem> items = new ArrayList<>();

    private boolean confirmado = false; // cuando el cliente envía la cotización

    public void agregarItem(Producto producto, int cantidad) {
        for (CarritoItem item : items) {
            if (item.getProducto().getId().equals(producto.getId())) {
                item.setCantidad(item.getCantidad() + cantidad);
                return;
            }
        }
        CarritoItem nuevo = new CarritoItem();
        nuevo.setProducto(producto);
        nuevo.setCantidad(cantidad);
        nuevo.setCarrito(this);
        items.add(nuevo);
    }

    public boolean eliminarItemPorId(Long idItem) {
        if (items == null || items.isEmpty())
            return false;

        Iterator<CarritoItem> iterator = items.iterator();
        while (iterator.hasNext()) {
            CarritoItem item = iterator.next();
            if (item.getId().equals(idItem)) {
                iterator.remove(); // elimina del Set/List
                return true;
            }
        }
        return false;
    }

    public void vaciar() {
        items.clear();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public List<CarritoItem> getItems() {
        return items;
    }

    public void setItems(List<CarritoItem> items) {
        this.items = items;
    }

    public boolean isConfirmado() {
        return confirmado;
    }

    public void setConfirmado(boolean confirmado) {
        this.confirmado = confirmado;
    }

    public boolean actualizarCantidad(Long idItem, Integer nuevaCantidad) {
        for (CarritoItem item : items) {
            if (item.getId().equals(idItem)) {
                item.setCantidad(nuevaCantidad);
                return true;
            }
        }
        return false;
    }

}

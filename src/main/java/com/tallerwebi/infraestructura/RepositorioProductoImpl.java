package com.tallerwebi.infraestructura;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.tallerwebi.dominio.entidades.Producto;
import com.tallerwebi.dominio.repositorios_interfaces.RepositorioGenerico;

@Repository("repositorioProducto")
public class RepositorioProductoImpl implements RepositorioGenerico<Producto>{
    private final Map<Long, Producto> database;
	private static Long proximoId;

    public RepositorioProductoImpl() {
		proximoId = 0L;
		this.database = new HashMap<>();
	}

    @Override
    public Producto obtener(Long id) {        
        return database.get(id);
    }

    @Override
    public List<Producto> obtener() {
        return new ArrayList<>(database.values());
    }

    @Override
    public Boolean guardar(Producto item) {
        item.setId(++proximoId);
		database.put(item.getId(), item); 
		return true;
    }

    @Override
    public Boolean actualizar(Producto item) {
        database.put(item.getId(), item); 
		return true;
    }

    @Override
    public void eliminar(Long id) {
        this.database.remove(id);
    }

    public Producto obtenerPorNombreMarcaYProveedor(String nombre, Integer marcaId, Integer proveedorId) {
		List<Producto> productos = new ArrayList<>(database.values());
		Producto productoBuscado = null;
		for(Producto producto : productos) {
			if(producto.getNombre().equals(nombre)&& producto.getMarcaId().equals(marcaId) && producto.getProveedorId().equals(proveedorId)) {
				productoBuscado = producto;
				break;
			}
		}
		return productoBuscado;
	}
}

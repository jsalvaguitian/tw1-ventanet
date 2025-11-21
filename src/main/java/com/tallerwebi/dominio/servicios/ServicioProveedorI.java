package com.tallerwebi.dominio.servicios;

import java.util.List;

import com.tallerwebi.dominio.entidades.MedioDePago;
import com.tallerwebi.dominio.entidades.Proveedor;
import com.tallerwebi.dominio.enums.EstadoUsuario;
import com.tallerwebi.dominio.enums.Rubro;
 
public interface ServicioProveedorI {
    List<Proveedor> obtenerTodosLosProveedoresActivos();
    List<Proveedor> obtenerTodosLosProveedoresPendientes();
    Proveedor buscarPorId(Long id);
    void actualizar(Proveedor proveedor);

    Proveedor obtenerPorIdUsuario(Long idUsuario);
    
    Integer contarProveedores(EstadoUsuario estado);
    List<Rubro> obtenerRubrosActivos();
    List<Proveedor> obtenerProveedoresPorRubro(Rubro rubro);
    List<Proveedor>obtenerProveedoresPorEstadoActivoInactivo(Boolean estado);
    List<MedioDePago> obtenerMediosDePagoDeProveedor(Long proveedorId);
    void actualizarMediosPago(Long proveedorId, List<Long> medioIds);
}
